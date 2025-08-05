package com.ca.gymbackend.challenge.service;

import com.ca.gymbackend.challenge.dto.payment.ChallengePayment;
import com.ca.gymbackend.challenge.dto.payment.KakaoPayApproveRequest;
import com.ca.gymbackend.challenge.dto.payment.KakaoPayApproveResponse;
import com.ca.gymbackend.challenge.dto.payment.KakaoPayReadyRequest;
import com.ca.gymbackend.challenge.dto.payment.KakaoPayReadyResponse;
import com.ca.gymbackend.challenge.dto.payment.PaymentReadyResponse;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {

    private final ChallengeMapper challengeMapper; // ChallengeMapper를 사용하여 DB 접근

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.pay.cid}")
    private String cid;

    @Value("${kakao.pay.admin-key}")
    private String adminKey;
    
    @Value("${kakao.pay.ready-url}")
    private String kakaoPayReadyUrl;

    @Value("${kakao.pay.approve-url}")
    private String kakaoPayApproveUrl;

    @Value("${kakao.pay.approval-url}")
    private String kakaoPayApprovalUrl;

    @Value("${kakao.pay.cancel-url}")
    private String kakaoPayCancelUrl;
    
    @Value("${kakao.pay.fail-url}")
    private String kakaoPayFailUrl;

    // 카카오페이 결제 준비
    public PaymentReadyResponse kakaoPayReady(int challengeId, int userId) {
        
        // 챌린지 정보 조회
        Integer amount = challengeMapper.findChallengeDepositAmount(challengeId);
        
        // null 체크 로직 추가
        if (amount == null) {
            // 챌린지 정보가 없거나 보증금 정보가 없는 경우의 예외 처리
            throw new IllegalArgumentException("챌린지 ID에 해당하는 보증금 정보를 찾을 수 없습니다.");
        }

        String challengeTitle = challengeMapper.findChallengeTitleById(challengeId);

        

        // KakaoPayReadyRequest DTO 생성
        KakaoPayReadyRequest request = new KakaoPayReadyRequest();
        request.setCid(cid);
        request.setPartner_order_id(String.valueOf(challengeId));
        request.setPartner_user_id(String.valueOf(userId));
        request.setItem_name(challengeTitle);
        request.setTotal_amount(amount);
        request.setTax_free_amount(0);
        
        // approval_url에 쿼리 파라미터 추가
        String approvalUrlWithParams = String.format("%s?challengeId=%d&userId=%d", 
                                                      kakaoPayApprovalUrl,
                                                      challengeId,
                                                      userId);
        
        // application.properties에 정의된 URL을 사용
        request.setApproval_url(approvalUrlWithParams);
        request.setCancel_url(kakaoPayCancelUrl);
        request.setFail_url(kakaoPayFailUrl);

        // HTTP Headers 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + adminKey);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        HttpEntity<KakaoPayReadyRequest> entity = new HttpEntity<>(request, headers);

        // 카카오페이 API 호출
        KakaoPayReadyResponse response = restTemplate.postForObject(
                kakaoPayReadyUrl,
                entity,
                KakaoPayReadyResponse.class
        );

        // 결제 정보 DB 저장 (status: READY)
        ChallengePayment challengePayment = new ChallengePayment();
        challengePayment.setUserId(userId);
        challengePayment.setChallengeId(challengeId);
        challengePayment.setChallengePaymentAmount(amount);
        challengePayment.setChallengePaymentStatus("READY");
        challengePayment.setChallengePaymentTid(response.getTid());
        challengeMapper.insertPayment(challengePayment);

        PaymentReadyResponse readyResponse = new PaymentReadyResponse();
        readyResponse.setTid(response.getTid());
        readyResponse.setRedirectUrl(response.getNext_redirect_pc_url()); // PC 환경의 경우
        // 또는 response.getNext_redirect_mobile_url()

        return readyResponse;
    }
    

    // 카카오페이 결제 승인
    public boolean kakaoPayApprove(String tid, int challengeId, int userId, String pgToken) {
        // HTTP Headers 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + adminKey);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        // KakaoPayApproveRequest DTO 생성
        KakaoPayApproveRequest request = new KakaoPayApproveRequest();
        request.setCid(cid);
        request.setTid(tid);
        request.setPartner_order_id(String.valueOf(challengeId));
        request.setPartner_user_id(String.valueOf(userId));
        request.setPg_token(pgToken);
        
        HttpEntity<KakaoPayApproveRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            // 카카오페이 API 호출
            KakaoPayApproveResponse response = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/approve",
                    entity,
                    KakaoPayApproveResponse.class
            );

            // payment 테이블 상태 업데이트
            challengeMapper.updatePaymentStatus(tid, "COMPLETED", pgToken);

            // 결제 성공 로직은 PaymentServiceImpl에서 하지 않고, ChallengeServiceImpl에 위임하는 것이 더 좋습니다.
            // 여기서는 단순히 결제 상태만 업데이트하고 true를 반환합니다.

            return true;

        } catch (Exception e) {
            // 결제 승인 실패 시 처리
            challengeMapper.updatePaymentStatus(tid, "CANCELED", null);
            return false;
        }
    }
    

    // TID를 이용해 결제 정보를 조회 (결제 승인 로직에 필요)
    public String getReadyTid(int userId, int challengeId) {
        return challengeMapper.findReadyTidByUserIdAndChallengeId(userId, challengeId);
    }
}