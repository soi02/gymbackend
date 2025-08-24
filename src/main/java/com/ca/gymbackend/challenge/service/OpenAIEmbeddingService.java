package com.ca.gymbackend.challenge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIEmbeddingService {

    private final WebClient openAiWebClient;

    private static final String MODEL = "text-embedding-3-small";

    /** 텍스트 1건 임베딩 (단건) */
    public List<Double> embedOne(String text) {
        if (text == null || text.isBlank()) return List.of();
        try {
            var req = new EmbeddingRequest(MODEL, text);
            var res = openAiWebClient.post()
                    .uri("/embeddings")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(EmbeddingResponse.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(200))
                            .filter(this::isRetryable))
                    .block();

            if (res == null || res.data == null || res.data.isEmpty()) return List.of();
            return res.data.get(0).embedding != null ? res.data.get(0).embedding : List.of();

        } catch (WebClientResponseException.Unauthorized e) {
            log.error("❌ OpenAI 인증 오류: 유효하지 않은 API 키", e);
            return List.of();
        } catch (WebClientResponseException.TooManyRequests e) {
            log.warn("❌ OpenAI 429: 호출 제한 초과", e);
            return List.of();
        } catch (Exception e) {
            log.error("❌ OpenAI 호출 예외", e);
            return List.of();
        }
    }

    /** 여러 문장을 배치 임베딩 (안전/재시도 포함) */
    public List<List<Double>> embedBatch(List<String> inputs) {
        if (inputs == null || inputs.isEmpty()) return List.of();
        try {
            var req = new EmbeddingRequest(MODEL, inputs);
            var res = openAiWebClient.post()
                    .uri("/embeddings")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(EmbeddingResponse.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(200))
                            .filter(this::isRetryable))
                    .block();

            if (res == null || res.data == null) return List.of();

            List<List<Double>> out = new ArrayList<>(res.data.size());
            for (var d : res.data) out.add(d.embedding != null ? d.embedding : List.of());
            return out;

        } catch (Exception e) {
            log.error("❌ OpenAI 배치 임베딩 실패", e);
            return List.of();
        }
    }

    private boolean isRetryable(Throwable t) {
        if (t instanceof WebClientResponseException w) {
            int s = w.getStatusCode().value();
            return s == 429 || (s >= 500 && s < 600);
        }
        return true; // 네트워크 계열
    }

    /** 코사인 유사도 */
    public static double cosine(List<Double> a, List<Double> b) {
        if (a == null || b == null) return 0.0;
        int n = Math.min(a.size(), b.size());
        if (n == 0) return 0.0;

        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < n; i++) {
            double x = a.get(i), y = b.get(i);
            dot += x * y; na += x * x; nb += y * y;
        }
        double denom = Math.sqrt(na) * Math.sqrt(nb);
        return denom == 0 ? 0.0 : dot / denom;
    }

    // --- DTO (간단 레코드) ---
    public record EmbeddingRequest(String model, Object input) {}
    public record EmbeddingData(List<Double> embedding, int index, String object) {}
    public record EmbeddingResponse(String model, String object, List<EmbeddingData> data) {}


// chunk 버전 추가
    public List<List<Double>> embedBatchChunked(List<String> inputs, int chunkSize) {
    if (inputs == null || inputs.isEmpty()) return List.of();
    if (chunkSize <= 0) chunkSize = 8;

    List<List<Double>> all = new ArrayList<>(inputs.size());
    for (int i = 0; i < inputs.size(); i += chunkSize) {
        int end = Math.min(i + chunkSize, inputs.size());
        var slice = inputs.subList(i, end);
        var part = embedBatch(slice); // 기존 배치 메서드 재사용
        if (part == null || part.size() != slice.size()) {
            // 실패한 조각은 빈 벡터로 채우거나, 필요 시 재시도 로직 추가
            for (int k = 0; k < slice.size(); k++) all.add(List.of());
        } else {
            all.addAll(part);
        }
    }
    return all;
}
}
