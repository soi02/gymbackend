package com.ca.gymbackend.challenge.service;

import com.ca.gymbackend.challenge.dto.payment.ChallengePayment;
import com.ca.gymbackend.challenge.dto.payment.KakaoPayApproveResponse;
import com.ca.gymbackend.challenge.dto.payment.KakaoPayReadyResponse;
import com.ca.gymbackend.challenge.dto.payment.PaymentReadyResponse;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    // 프론트엔드 애플리케이션의 기본 URL (Vite 개발 서버 기준)
    @Value("${frontend.base-url}")
    private String frontEndBaseUrl;

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

        // ⭐ 수정된 부분: PC 및 모바일 리다이렉트 URL 모두 담아서 전달
        // 기존의 redirectUrl 필드에 PC URL을 할당합니다.
        frontEndResponse.setRedirectUrl(kakaoResponse.getNext_redirect_pc_url());
        // 새로 추가된 next_redirect_mobile_url 필드에 모바일 URL을 할당합니다.
        frontEndResponse.setNext_redirect_mobile_url(kakaoResponse.getNext_redirect_mobile_url());

        return frontEndResponse;
    } catch (HttpClientErrorException e) {
        System.err.println("카카오페이 결제 준비 실패: " + e.getResponseBodyAsString());
        throw new RuntimeException("카카오페이 결제 준비 중 오류가 발생했습니다.", e);
    }
}
    /**
     * 카카오페이 결제 승인 및 챌린지 참가 처리
     * 결제 성공/실패에 따라 프론트엔드 URL로 리다이렉션 응답을 반환합니다.
     * @return ResponseEntity<Void> (본문 없이 HTTP 상태 코드와 헤더로 응답)
     */
    @Transactional
    public ResponseEntity<Void> kakaoPayApprove(Long challengeId, int userId, String pgToken) {
        String existingSuccessTid = challengeMapper.findSuccessTidByUserIdAndChallengeId(userId, challengeId.intValue());

        HttpHeaders headers = new HttpHeaders();

        if (existingSuccessTid != null) {
            // 이미 성공한 결제이므로, 프론트엔드의 성공 페이지로 리다이렉션
            headers.add("Location", frontEndBaseUrl + "/gymmadang/challenge/payment/success?challengeId=" + challengeId + "&userId=" + userId + "&status=success&message=already_processed");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        String readyTid = challengeMapper.findReadyTidByUserIdAndChallengeId(userId, challengeId.intValue());

        if (readyTid == null) {
            // 결제 준비 상태가 아닌 경우, 프론트엔드의 실패 페이지로 리다이렉션
            headers.add("Location", frontEndBaseUrl + "/gymmadang/challenge/payment/fail?message=no_ready_payment_found");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        HttpHeaders kakaoHeaders = new HttpHeaders();
        kakaoHeaders.setContentType(MediaType.APPLICATION_JSON);
        kakaoHeaders.set("Authorization", "SECRET_KEY " + secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("cid", cid);
        body.put("tid", readyTid);
        body.put("partner_order_id", challengeId.toString());
        body.put("partner_user_id", String.valueOf(userId));
        body.put("pg_token", pgToken);

        try {
            KakaoPayApproveResponse kakaoResponse = restClient.post()
                    .uri("/online/v1/payment/approve")
                    .headers(h -> h.addAll(kakaoHeaders))
                    .body(body)
                    .retrieve()
                    .body(KakaoPayApproveResponse.class);

            if (kakaoResponse != null) {
                // 1. DB에 결제 상태 'SUCCESS'로 업데이트
                challengeMapper.updatePaymentStatus(readyTid, "SUCCESS", pgToken);

                // 🌟 추가된 로직 시작 🌟
                // 2. user_challenge 테이블에 참가자 정보 삽입
                challengeMapper.insertUserChallengeInfo(userId, challengeId.intValue());

                // 3. challenge 테이블의 참가자 수 1 증가
                challengeMapper.increaseChallengeParticipantCountInfo(challengeId.intValue());
                // 🌟 추가된 로직 끝 🌟

                // 4. 성공 페이지로 리다이렉트
                headers.add("Location", frontEndBaseUrl + "/gymmadang/challenge/payment/success?challengeId=" + challengeId + "&userId=" + userId + "&status=success");
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }
            throw new RuntimeException("카카오페이 결제 승인 응답이 유효하지 않습니다.");

        } catch (HttpClientErrorException e) {
            // 결제 실패 시, 결제 상태를 'FAIL'로 업데이트하는 로직도 추가하는 것이 좋습니다.
            // challengeMapper.updatePaymentStatus(readyTid, "FAIL");

            // 실패 페이지로 리다이렉트
            headers.add("Location", frontEndBaseUrl + "/gymmadang/challenge/payment/fail?message=payment_failed");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }
}