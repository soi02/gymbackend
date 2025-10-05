package com.ca.gymbackend.challenge.controller;

import com.ca.gymbackend.challenge.dto.ChallengeFinalTestResult;
import com.ca.gymbackend.challenge.dto.ChallengeListResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.service.BudgetGuardService;
import com.ca.gymbackend.challenge.service.ChallengeServiceImpl;
import com.ca.gymbackend.challenge.service.OpenAIEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/recommend")
@Slf4j
public class ChallengeAIController {

    private final OpenAIEmbeddingService embedding;
    private final ChallengeServiceImpl challengeService;
    private final BudgetGuardService budget;     // ★ 예산 가드 주입

    // ------- 캐시 -------
    private final Cache<String, List<ChallengeListResponse>> userRecCache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(6)).maximumSize(10_000).build();
    private final Cache<Integer, List<Double>> challengeVecCache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofDays(30)).maximumSize(50_000).build();
    private final Cache<Integer, String> challengeTextHashCache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofDays(30)).maximumSize(50_000).build();
    private final Cache<String, List<Double>> userVecCache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(12)).maximumSize(200_000).build();

    private static final double MIN_SCORE = 0.25;

    @GetMapping("/ai")
    public List<ChallengeListResponse> recommendForUser(
            @RequestParam("userId") int userId,
            @RequestParam(value = "topN", defaultValue = "5") int topN
    ) {
        // 기존
        // 결과 캐시 키에 유저 프로필 해시 포함해서 정합성 높이려고 변경함. 기존꺼는 유저 프로필이 바뀌어도 6시간동안 예전 추천이 나올 수 있음.
        //  final String recKey = userId + ":" + topN;

        // 개선
        String profile = buildUserProfileTextSlim(userId);
        String profileKey = userId + ":" + md5(profile);
        final String recKey = profileKey + ":" + topN;

        // 0) 결과 캐시 (버튼 연타 방지)
        var cached = userRecCache.getIfPresent(recKey);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }

        try {
            // 1) 전체 수련 로드
            List<ChallengeListResponse> all = challengeService.getAllChallengesWithKeywords();
            if (all == null || all.isEmpty()) return List.of();

            // 2) 예산 가드: 임베딩 호출이 필요한 상황이면 먼저 체크
            boolean needAnyEmbedding = needsAnyEmbedding(all, userId);
            if (needAnyEmbedding && budget.overBudget()) {
                log.warn("[REC] Budget mode → heuristic fallback");
                List<ChallengeListResponse> fb = heuristicFallback(userId, all, topN);
                userRecCache.put(recKey, fb);
                return fb;
            }
            // 3) 수련 텍스트 다이어트 + 변경분만 임베딩
            List<String> toEmbedTexts = new ArrayList<>();
            List<Integer> toEmbedIds   = new ArrayList<>();
            for (ChallengeListResponse c : all) {
                String slim = buildChallengeTextSlim(c);
                String h = md5(slim);
                boolean needEmbed = challengeVecCache.getIfPresent(c.getChallengeId()) == null
                        || !h.equals(challengeTextHashCache.getIfPresent(c.getChallengeId()));
                if (needEmbed) {
                    toEmbedTexts.add(slim);
                    toEmbedIds.add(c.getChallengeId());
                }
            }
            if (!toEmbedTexts.isEmpty()) {
                budget.inc(); // 임베딩 실제로 부를 때만 카운트
                List<List<Double>> newVecs = embedding.embedBatchChunked(toEmbedTexts, 16);
                for (int i = 0; i < toEmbedIds.size(); i++) {
                    challengeVecCache.put(toEmbedIds.get(i), newVecs.get(i));
                    challengeTextHashCache.put(toEmbedIds.get(i), md5(toEmbedTexts.get(i)));
                }
            }

            // 4) 유저 벡터 (12h 캐시)
            List<Double> userVec = userVecCache.getIfPresent(profileKey);
            if (userVec == null || userVec.isEmpty()) {
                budget.inc();
                userVec = embedding.embedOne(profile);
                userVecCache.put(profileKey, userVec);
            }

            // 5) 점수 계산
            record Scored(ChallengeListResponse c, double s) {}
            List<Scored> scored = new ArrayList<>(all.size());
            for (ChallengeListResponse c : all) {
                List<Double> v = challengeVecCache.getIfPresent(c.getChallengeId());
                if (v == null || v.isEmpty()) continue;
                scored.add(new Scored(c, OpenAIEmbeddingService.cosine(userVec, v)));
            }

            scored.stream()
                    .sorted(Comparator.comparingDouble(Scored::s).reversed())
                    .limit(10)
                    .forEach(s -> log.info("[AI-REC] id={} title='{}' score={}",
                            s.c().getChallengeId(), s.c().getChallengeTitle(), String.format("%.4f", s.s())));

            List<ChallengeListResponse> out = scored.stream()
                    .filter(x -> x.s() >= MIN_SCORE)
                    .sorted(Comparator.comparingDouble(Scored::s).reversed())
                    .limit(Math.max(1, topN))
                    .map(Scored::c).toList();

            if (out.isEmpty()) {
                out = scored.stream()
                        .sorted(Comparator.comparingDouble(Scored::s).reversed())
                        .limit(Math.max(1, topN))
                        .map(Scored::c).toList();
            }

            userRecCache.put(recKey, out);
            return out;

        } catch (Exception e) {
            log.error("AI 추천 실패 → 기본 리스트 fallback", e);
            List<ChallengeListResponse> all = challengeService.getAllChallengesWithKeywords();
            return fallback(all, topN);
        }
    }

    /** 지금 요청에서 임베딩이 '필요할 것 같은지' 대략 검사 (수련/유저 캐시 미보유 여부) */
    private boolean needsAnyEmbedding(List<ChallengeListResponse> all, int userId) {
        // 수련 임베딩 중 하나라도 없으면 true
        for (ChallengeListResponse c : all) {
            String slim = buildChallengeTextSlim(c);
            String h = md5(slim);
            if (challengeVecCache.getIfPresent(c.getChallengeId()) == null
                    || !h.equals(challengeTextHashCache.getIfPresent(c.getChallengeId()))) {
                return true;
            }
        }
        // 유저 벡터 없으면 true
        String profileKey = userId + ":" + md5(buildUserProfileTextSlim(userId));
        return userVecCache.getIfPresent(profileKey) == null;
    }

    // --------- 휴리스틱 폴백 (예산 초과 시) ---------
    private List<ChallengeListResponse> heuristicFallback(int userId, List<ChallengeListResponse> all, int topN) {
        // 유저 프로필 텍스트(슬림)에서 토큰 추출
        String profile = buildUserProfileTextSlim(userId).toLowerCase(Locale.ROOT);
        Set<String> userTokens = new HashSet<>(Arrays.asList(profile.split("\\s+")));

        record Scored(ChallengeListResponse c, int s) {}
        List<Scored> scored = new ArrayList<>(all.size());
        for (var c : all) {
            int score = 0;
            if (c.getKeywords() != null) {
                for (String kw : c.getKeywords()) {
                    if (kw != null && userTokens.contains(kw.toLowerCase(Locale.ROOT))) score += 2;
                }
            }
            if (c.getChallengeTitle() != null) {
                for (String t : c.getChallengeTitle().toLowerCase(Locale.ROOT).split("\\s+")) {
                    if (userTokens.contains(t)) score += 1;
                }
            }
            scored.add(new Scored(c, score));
        }
        return scored.stream()
                .sorted(Comparator.comparingInt(Scored::s).reversed())
                .limit(Math.max(1, topN))
                .map(Scored::c)
                .toList();
    }

    // --------- 기존 보조 메서드들 ---------
    private List<ChallengeListResponse> fallback(List<ChallengeListResponse> all, int topN) {
        if (all == null || all.isEmpty()) return List.of();
        return all.stream().limit(Math.max(1, topN)).toList();
    }

    /** 유저 프로필 텍스트 (슬림) */
    private String buildUserProfileTextSlim(int userId) {
        StringBuilder sb = new StringBuilder();
        try {
            ChallengeFinalTestResult t = challengeService.findTestResult(userId);
            if (t != null) {
                if (t.getTopType1() != null) sb.append(t.getTopType1()).append(' ');
                if (t.getTopType2() != null) sb.append(t.getTopType2()).append(' ');
            }
        } catch (Exception ignored) {}

        try {
            List<ChallengeMyRecordsResponse> my = challengeService.getAllMyChallengeList(userId);
            sb.append("joinedCount ").append(my.size()).append(". ");
            int count = 0;
            for (var r : my) {
                if (r.getChallengeTitle() != null && count < 3) { // 3개만 넣어 토큰 절감
                    sb.append(r.getChallengeTitle()).append(' ');
                    count++;
                }
            }
        } catch (Exception ignored) {}

        String out = sb.toString().trim();
        return out.isEmpty()
                ? "balanced healthy habit routine recovery learning strength stretch cardio"
                : out;
    }

    /** 수련 텍스트 (슬림: 제목 + 키워드 3개) */
    private String buildChallengeTextSlim(ChallengeListResponse c) {
        StringBuilder sb = new StringBuilder();
        if (c.getChallengeTitle() != null) sb.append(c.getChallengeTitle()).append(' ');
        if (c.getKeywords() != null) {
            int k = 0;
            for (String kw : c.getKeywords()) {
                if (kw != null && !kw.isBlank()) {
                    sb.append(kw).append(' ');
                    if (++k == 3) break;
                }
            }
        }
        return sb.toString().trim();
    }

    /** MD5 해시 (짧은 캐시 키) */
    private static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] d = md.digest((s == null ? "" : s).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(d);
        } catch (Exception e) {
            return Integer.toHexString(Objects.hashCode(s));
        }
    }
}
