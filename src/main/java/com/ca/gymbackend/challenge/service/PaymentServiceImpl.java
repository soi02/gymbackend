package com.ca.gymbackend.challenge.service;

import com.ca.gymbackend.challenge.dto.payment.ChallengePayment;
import com.ca.gymbackend.challenge.dto.payment.KakaoPayApproveResponse;
import com.ca.gymbackend.challenge.dto.payment.KakaoPayReadyResponse;
import com.ca.gymbackend.challenge.dto.payment.PaymentReadyResponse;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {

    @Value("${kakao.pay.secret-key}")
    private String secretKey;

    @Value("${kakao.pay.cid}")
    private String cid;

    @Value("${kakao.pay.approval-url}")
    private String approvalUrl;

    @Value("${kakao.pay.cancel-url}")
    private String cancelUrl;

    @Value("${kakao.pay.fail-url}")
    private String failUrl;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://open-api.kakaopay.com")
            .build();
    
    private final ChallengeMapper challengeMapper; // Mybatis를 위한 Mapper 주입

    // 결제 준비
    public PaymentReadyResponse kakaoPayReady(Long challengeId, int userId, String challengeTitle, int totalAmount) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("cid", cid);
        body.put("partner_order_id", challengeId.toString());
        body.put("partner_user_id", String.valueOf(userId));
        body.put("item_name", challengeTitle);
        body.put("quantity", 1);
        body.put("total_amount", totalAmount);
        body.put("tax_free_amount", 0);
        body.put("approval_url", approvalUrl + "?challengeId=" + challengeId + "&userId=" + userId);
        body.put("cancel_url", cancelUrl);
        body.put("fail_url", failUrl);

        try {
            KakaoPayReadyResponse kakaoResponse = restClient.post()
                    .uri("/online/v1/payment/ready")
                    .headers(h -> h.addAll(headers))
                    .body(body)
                    .retrieve()
                    .body(KakaoPayReadyResponse.class);

            if (kakaoResponse == null) {
                throw new RuntimeException("카카오페이 결제 준비 응답이 비어 있습니다.");
            }

            // DB에 'READY' 상태로 결제 정보 저장
            ChallengePayment payment = new ChallengePayment();
            payment.setUserId(userId);
            payment.setChallengeId(challengeId.intValue());
            payment.setChallengePaymentAmount(totalAmount);
            payment.setChallengePaymentStatus("READY");
            payment.setChallengePaymentTid(kakaoResponse.getTid());
            challengeMapper.insertPayment(payment);

            // 프론트엔드 응답용 DTO 생성
            PaymentReadyResponse frontEndResponse = new PaymentReadyResponse();
            frontEndResponse.setTid(kakaoResponse.getTid());
            frontEndResponse.setRedirectUrl(kakaoResponse.getNext_redirect_pc_url());

            return frontEndResponse;
        } catch (HttpClientErrorException e) {
            System.err.println("카카오페이 결제 준비 실패: " + e.getResponseBodyAsString());
            throw new RuntimeException("카카오페이 결제 준비 중 오류가 발생했습니다.", e);
        }
    }

    // 결제 승인
    public boolean kakaoPayApprove(Long challengeId, int userId, String pgToken) {
        // Mybatis를 사용해 DB에서 READY 상태의 tid를 가져옴
        String tid = challengeMapper.findReadyTidByUserIdAndChallengeId(userId, challengeId.intValue());
        
        if (tid == null) {
            throw new IllegalStateException("결제 준비 상태의 TID가 존재하지 않습니다.");
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("cid", cid);
        body.put("tid", tid);
        body.put("partner_order_id", challengeId.toString());
        body.put("partner_user_id", String.valueOf(userId));
        body.put("pg_token", pgToken);

        try {
            KakaoPayApproveResponse kakaoResponse = restClient.post()
                    .uri("/online/v1/payment/approve")
                    .headers(h -> h.addAll(headers))
                    .body(body)
                    .retrieve()
                    .body(KakaoPayApproveResponse.class);

            if (kakaoResponse != null) {
                // 결제 승인 성공 시 DB 상태 업데이트
                challengeMapper.updatePaymentStatus(tid, "SUCCESS", pgToken);
                return true;
            }
            return false;
        } catch (HttpClientErrorException e) {
            System.err.println("카카오페이 결제 승인 실패: " + e.getResponseBodyAsString());
            // 결제 실패 시 DB 상태 업데이트 (선택 사항)
            challengeMapper.updatePaymentStatus(tid, "FAILED", pgToken);
            return false;
        }
    }
}