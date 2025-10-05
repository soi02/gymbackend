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

    private final OpenAIEmbeddingService embedding; // OpenAI 임베딩 호출 담당
    private final ChallengeServiceImpl challengeService; // DB 에서 챌린지, 유저 정보 가져오는 서비스
    private final BudgetGuardService budget; // 예산 가드 (한도 초과 체크)

    // ------- 캐시 -------
    // 유저별 추천 결과 캐시 (최종 목록) : 6시간
    private final Cache<String, List<ChallengeListResponse>> userRecCache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(6)).maximumSize(10_000).build();
    
    // 챌린지 임베딩 벡터 캐시 : 30일
    private final Cache<Integer, List<Double>> challengeVecCache =
    Caffeine.newBuilder().expireAfterWrite(Duration.ofDays(30)).maximumSize(50_000).build();

    // 챌린지 텍스트 해시 캐시 (내용 바뀜 감지) : 30일
    private final Cache<Integer, String> challengeTextHashCache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofDays(30)).maximumSize(50_000).build();

    // 유저 임베딩 벡터 캐시 : 12시간 (키 = userId + 프로필 해시)
    private final Cache<String, List<Double>> userVecCache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(12)).maximumSize(200_000).build();

    // 추천 커트라인 (최소 점수) - 비어버리면 우선순위 상위로 대체
    private static final double MIN_SCORE = 0.25;

    @GetMapping("/ai")
    public List<ChallengeListResponse> recommendForUser(
            @RequestParam("userId") int userId,
            @RequestParam(value = "topN", defaultValue = "5") int topN
    ) {
        // 기존: userId:topN  → 프로필이 바뀌어도 6시간 동안 예전 추천이 나감.
        //  final String recKey = userId + ":" + topN;

        // 개선: userId + 프로필 해시 + topN  → 프로필 변경 즉시 다른 키가 되어 최신화.
        String profile = buildUserProfileTextSlim(userId); // 유저를 간단히 대표하는 텍스트
        String profileKey = userId + ":" + md5(profile); // 유저 프로필 해시
        final String recKey = profileKey + ":" + topN; // 결과 캐시 키

        // 0) 결과 캐시 조회 - 버튼 연타, 같은 요청 반복 시 여기서 바로 리턴
        var cached = userRecCache.getIfPresent(recKey);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }

        try {
            // 1) 전체 챌린지 로드 (제목, 키워드 등)
            List<ChallengeListResponse> all = challengeService.getAllChallengesWithKeywords();
            if (all == null || all.isEmpty()) return List.of();

            // 2) 예산 가드: 이번 요처어이 임베딩을 새로 호출해야만 하는 상황인지 미리 판단
            // - 챌린지 벡터가 없거나 (또는 텍스트가 바뀜)
            // - 유저 벡터가 없으면 -> 임베딩 호출 필요
            boolean needAnyEmbedding = needsAnyEmbedding(all, userId);
            // 임베딩이 필요한데 예산이 이미 초과 -> 안전한 휴리스틱 폴백으로 대체
            if (needAnyEmbedding && budget.overBudget()) {
                log.warn("[REC] Budget mode → heuristic fallback");
                List<ChallengeListResponse> fb = heuristicFallback(userId, all, topN);
                userRecCache.put(recKey, fb); // 폴백 결과도 잠시 캐시
                return fb;
            }
            // 3) 챌린지 텍스트 슬림하게 만들고, 변경된 것만 임베딩 (비용절감 핵심!!)
            List<String> toEmbedTexts = new ArrayList<>();
            List<Integer> toEmbedIds   = new ArrayList<>();
            for (ChallengeListResponse c : all) {
                String slim = buildChallengeTextSlim(c); // 제목 + 상위 키워드 3개 정도로 요약
                String h = md5(slim); // 텍스트 해시
                boolean needEmbed = challengeVecCache.getIfPresent(c.getChallengeId()) == null // 벡터 없거나
                        || !h.equals(challengeTextHashCache.getIfPresent(c.getChallengeId())); // 텍스트가 바뀐 경우
                if (needEmbed) {
                    toEmbedTexts.add(slim);
                    toEmbedIds.add(c.getChallengeId());
                }
            }
            // 변경된 것만 배치 임베딩
            if (!toEmbedTexts.isEmpty()) {
                budget.inc(); // 실제 임베딩 호출 시에만 비용 카운트 증가
                List<List<Double>> newVecs = embedding.embedBatchChunked(toEmbedTexts, 16); // 안전한 청크 호출
                for (int i = 0; i < toEmbedIds.size(); i++) {
                    challengeVecCache.put(toEmbedIds.get(i), newVecs.get(i)); // 새 벡터 저장
                    challengeTextHashCache.put(toEmbedIds.get(i), md5(toEmbedTexts.get(i))); // 최신 해시 저장
                }
            }

            // 4) 유저 임베딩 벡터 (12시간 캐시). 캐시에 없으면 한 번만 생성
            List<Double> userVec = userVecCache.getIfPresent(profileKey);
            if (userVec == null || userVec.isEmpty()) {
                budget.inc(); // 유저 임베딩도 비용 카운트
                userVec = embedding.embedOne(profile);
                userVecCache.put(profileKey, userVec);
            }

            // 5) 유사도 점수 계산 (코사인 유사도) -> 정렬 -> 상위 N 개 추출
            record Scored(ChallengeListResponse c, double s) {}
            List<Scored> scored = new ArrayList<>(all.size());
            for (ChallengeListResponse c : all) {
                List<Double> v = challengeVecCache.getIfPresent(c.getChallengeId()); // 아이템 벡터
                if (v == null || v.isEmpty()) continue; // 벡터 없으면 스킵
                scored.add(new Scored(c, OpenAIEmbeddingService.cosine(userVec, v))); // 코사인 유사도
            }

            // 디버깅 : 상위 10개 점수 로그로 남겨 확인
            scored.stream()
                    .sorted(Comparator.comparingDouble(Scored::s).reversed())
                    .limit(10)
                    .forEach(s -> log.info("[AI-REC] id={} title='{}' score={}",
                            s.c().getChallengeId(), s.c().getChallengeTitle(), String.format("%.4f", s.s())));

            // 커트라인 이상만 우선 선택
            List<ChallengeListResponse> out = scored.stream()
                    .filter(x -> x.s() >= MIN_SCORE)
                    .sorted(Comparator.comparingDouble(Scored::s).reversed())
                    .limit(Math.max(1, topN))
                    .map(Scored::c).toList();

            // 만약 커트라인 통과가 하나도 없으면(새 계정 등의 경우) -> 점수 상위 N개로 보정
            if (out.isEmpty()) {
                out = scored.stream()
                        .sorted(Comparator.comparingDouble(Scored::s).reversed())
                        .limit(Math.max(1, topN))
                        .map(Scored::c).toList();
            }

            // 최종 결과 캐시 (6시간) - 같은 프로필이면 재사용
            userRecCache.put(recKey, out);
            return out;

        } catch (Exception e) {
            // 어떤 예외가 나도 추천 서비스는 죽지 않도록 기본 리스트로 폴백
            log.error("AI 추천 실패 → 기본 리스트 fallback", e);
            List<ChallengeListResponse> all = challengeService.getAllChallengesWithKeywords();
            return fallback(all, topN);
        }
    }

    // 이번 요청에서 임베딩을 새로 불러야 하는지 가볍게 판단
    // - 챌린지 벡터가 없거나 텍스트가 바뀌었으면 true
    // - 유저 벡터가 없으면 true
    private boolean needsAnyEmbedding(List<ChallengeListResponse> all, int userId) {
        // 챌린지들 중 하나라도 벡터 없거나 텍스트 변경되면 true
        for (ChallengeListResponse c : all) {
            String slim = buildChallengeTextSlim(c);
            String h = md5(slim);
            if (challengeVecCache.getIfPresent(c.getChallengeId()) == null
                    || !h.equals(challengeTextHashCache.getIfPresent(c.getChallengeId()))) {
                return true;
            }
        }
        // 유저 벡터 없으면 true (여기선 로컬로 다시 계산)
        String profileKey = userId + ":" + md5(buildUserProfileTextSlim(userId));
        return userVecCache.getIfPresent(profileKey) == null;
    }

    // --------- 휴리스틱 폴백 (예산 초과 시) ---------
    // 예산 초과 시에만 쓰는 간단 매칭 폴백
    // - 유저 프로필의 단어들과 챌린지 제목, 키워드가 얼마나 겹치는지 점수화
    // - 임베딩 호출 없이 빠르게 상위 N개 고른다
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
                    if (kw != null && userTokens.contains(kw.toLowerCase(Locale.ROOT))) score += 2; // 키워드 일치 가중치 ↑
                }
            }
            if (c.getChallengeTitle() != null) {
                for (String t : c.getChallengeTitle().toLowerCase(Locale.ROOT).split("\\s+")) {
                    if (userTokens.contains(t)) score += 1; // 제목 단어 일치
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
    // 기본 폴백 (그냥 앞에서부터 N개) - 심각한 예외 시 안전장치
    private List<ChallengeListResponse> fallback(List<ChallengeListResponse> all, int topN) {
        if (all == null || all.isEmpty()) return List.of();
        return all.stream().limit(Math.max(1, topN)).toList();
    }

    // 유저를 대표하는 짧은 텍스트 구성 - 토큰, 비용 절감을 위해 최소 정보만
    private String buildUserProfileTextSlim(int userId) {
        StringBuilder sb = new StringBuilder();
        try {
            // 성향 테스트 결과 (상위 2개 타입) -> 키워드처럼 사용
            ChallengeFinalTestResult t = challengeService.findTestResult(userId);
            if (t != null) {
                if (t.getTopType1() != null) sb.append(t.getTopType1()).append(' ');
                if (t.getTopType2() != null) sb.append(t.getTopType2()).append(' ');
            }
        } catch (Exception ignored) {}

        try {
            // 내가 참여한 수련 제목 몇 개만 추가 (3개 제한) + 참여 수
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
        // 아무 정보도 없으면 기본 키워드로 대체 (콜드 스타트 방지)
        return out.isEmpty()
                ? "balanced healthy habit routine recovery learning strength stretch cardio"
                : out;
    }

    // 챌린지 텍스트(슬림): 제목 + 키워드 최대 3개 → 토큰/비용 절감
    private String buildChallengeTextSlim(ChallengeListResponse c) {
        StringBuilder sb = new StringBuilder();
        if (c.getChallengeTitle() != null) sb.append(c.getChallengeTitle()).append(' ');
        if (c.getKeywords() != null) {
            int k = 0;
            for (String kw : c.getKeywords()) {
                if (kw != null && !kw.isBlank()) {
                    sb.append(kw).append(' ');
                    if (++k == 3) break; // 3개까지만
                }
            }
        }
        return sb.toString().trim();
    }

    // MD5 해시 → 텍스트 변경 여부를 짧은 문자열로 비교 (키로 쓰기 쉬움)
    private static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] d = md.digest((s == null ? "" : s).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(d); // 짧고 안전한 문자열화
        } catch (Exception e) {
            return Integer.toHexString(Objects.hashCode(s)); // 혹시 실패해도 대체값
        }
    }
}

// 임베딩 : 글, 문장을 숫자배열 (벡터)로 바꾸는 것. 비슷한 뜻이면 벡터도 서로 가깝다.
// 코사인 유사도 : 두 벡터 사이 방향이 얼마나 비슷한지. (0~1) 1에 가까울수록 더 유사함.
// 해시 (MD5) : 텍스트를 지문처럼 바꿀 수 있는 고정 길이 문자열. 내용이 바뀌면 해시도 바뀜.
// 캐시 : 계산, 호출 결과를 잠시 저장해두었다가 같은 요청이 오면 바로 꺼내쓰는 창고.
// TTL (만료시간) : 캐시를 너무 오래 들고 있지 않도록 정해둔 시간.