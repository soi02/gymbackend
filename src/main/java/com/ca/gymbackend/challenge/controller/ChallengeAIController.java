package com.ca.gymbackend.challenge.controller;

import com.ca.gymbackend.challenge.dto.ChallengeFinalTestResult;
import com.ca.gymbackend.challenge.dto.ChallengeListResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.service.ChallengeServiceImpl;
import com.ca.gymbackend.challenge.service.OpenAIEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/recommend")
@Slf4j
public class ChallengeAIController {

    private final OpenAIEmbeddingService embedding;
    private final ChallengeServiceImpl challengeService;

    // 🔑 임계값 완화 (필요시 0.20~0.30 사이로 조정)
    private static final double MIN_SCORE = 0.25;

    @GetMapping("/ai")
    public List<ChallengeListResponse> recommendForUser(
            @RequestParam("userId") int userId,
            @RequestParam(value = "topN", defaultValue = "5") int topN
    ) {
        try {
            // 1) 사용자 프로필 텍스트 & 전체 챌린지
            String userText = buildUserProfileText(userId);
            List<ChallengeListResponse> all = challengeService.getAllChallengesWithKeywords();
            if (all == null || all.isEmpty()) return List.of();

            // 2) 배치 임베딩: [user] + [challenges...]
            List<String> texts = new ArrayList<>(all.size() + 1);
            texts.add(userText);
            for (ChallengeListResponse c : all) texts.add(buildChallengeText(c));

            List<List<Double>> vecs = embedding.embedBatchChunked(texts, 8);
            if (vecs == null || vecs.size() < texts.size()) {
                log.warn("임베딩 결과 개수 불일치: expected={}, got={}", texts.size(), (vecs==null?0:vecs.size()));
                // 안전 fallback
                return fallback(all, topN);
            }

            List<Double> userVec = vecs.get(0);
            // 3) 점수 계산
            record Scored(ChallengeListResponse c, double score) {}
            List<Scored> scored = new ArrayList<>(all.size());
            for (int i = 0; i < all.size(); i++) {
                var c = all.get(i);
                var v = vecs.get(i + 1);
                double s = OpenAIEmbeddingService.cosine(userVec, v);
                scored.add(new Scored(c, s));
            }

            // 4) 상위 10개 로그로 확인
            scored.stream()
                    .sorted(Comparator.comparingDouble(Scored::score).reversed())
                    .limit(10)
                    .forEach(s -> log.info("[AI-REC] id={} title='{}' score={}",
                            s.c().getChallengeId(), s.c().getChallengeTitle(), String.format("%.4f", s.score())));

            // 5) 컷오프 → 비면 상위점수 강제 채우기
            List<ChallengeListResponse> byCutoff = scored.stream()
                    .filter(s -> s.score() >= MIN_SCORE)
                    .sorted(Comparator.comparingDouble(Scored::score).reversed())
                    .limit(Math.max(1, topN))
                    .map(Scored::c)
                    .toList();

            if (!byCutoff.isEmpty()) {
                log.info("AI recs size={} (cutoff={})", byCutoff.size(), MIN_SCORE);
                return byCutoff;
            }

            // 컷오프에 걸러서 0개면 상위 점수로 채우기
            List<ChallengeListResponse> topOnly = scored.stream()
                    .sorted(Comparator.comparingDouble(Scored::score).reversed())
                    .limit(Math.max(1, topN))
                    .map(Scored::c)
                    .toList();

            log.info("AI recs(size=0 after cutoff). Fallback to top-scores size={}", topOnly.size());
            return topOnly;

        } catch (Exception e) {
            log.error("AI 추천 실패 → 기본 리스트 fallback", e);
            List<ChallengeListResponse> all = challengeService.getAllChallengesWithKeywords();
            return fallback(all, topN);
        }
    }

    private List<ChallengeListResponse> fallback(List<ChallengeListResponse> all, int topN) {
        if (all == null || all.isEmpty()) return List.of();
        return all.stream().limit(Math.max(1, topN)).toList();
    }

    private String buildUserProfileText(int userId) {
        StringBuilder sb = new StringBuilder();

        // 성향 (있으면 추가)
        try {
            ChallengeFinalTestResult t = challengeService.findTestResult(userId);
            if (t != null) {
                if (t.getTopType1() != null) sb.append(t.getTopType1()).append(" ");
                if (t.getTopType2() != null) sb.append(t.getTopType2()).append(" ");
            }
        } catch (Exception ignored) {}

        // 참여 이력
        try {
            List<ChallengeMyRecordsResponse> my = challengeService.getAllMyChallengeList(userId);
            sb.append("joinedCount ").append(my.size()).append(". ");
            int count = 0;
            for (var r : my) {
                if (r.getChallengeTitle() != null && count < 5) {
                    sb.append(r.getChallengeTitle()).append(" ");
                    count++;
                }
            }
        } catch (Exception ignored) {}

        // 기본 시드 단어 (비었을 때 유사도 0 방지)
        String out = sb.toString().trim();
        return out.isEmpty() ? "balanced healthy habit routine recovery learning strength stretch cardio" : out;
    }

    /** 제목 + 기간/상태 + 키워드(List<String>) */
    private String buildChallengeText(ChallengeListResponse c) {
        StringBuilder sb = new StringBuilder();
        if (c.getChallengeTitle() != null) sb.append(c.getChallengeTitle()).append(". ");
        if (c.getChallengeDurationDays() > 0) sb.append("duration ").append(c.getChallengeDurationDays()).append(" days. ");
        if (c.getStatus() != null && !c.getStatus().isBlank()) sb.append("status ").append(c.getStatus()).append(". ");
        if (c.getKeywords() != null) {
            for (String kw : c.getKeywords()) {
                if (kw != null && !kw.isBlank()) sb.append(kw).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
