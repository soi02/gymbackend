package com.ca.gymbackend.challenge.dto.payment;

import lombok.Data;

// 결제 준비 요청

@Data
public class KakaoPayReadyRequest {
    private String cid; // 가맹점 코드 (테스트용: TC0ONETIME)
    private String partner_order_id; // 가맹점 주문번호 (challengeId)
    private String partner_user_id; // 가맹점 회원 ID (userId)
    private String item_name; // 상품명 (챌린지명)
    private Integer total_amount; // 상품 총액
    private Integer tax_free_amount; // 비과세 금액 (0으로 고정)
    private String approval_url; // 결제 성공 시 리다이렉트 URL
    private String cancel_url; // 결제 취소 시 리다이렉트 URL
    private String fail_url; // 결제 실패 시 리다이렉트 URL
}
