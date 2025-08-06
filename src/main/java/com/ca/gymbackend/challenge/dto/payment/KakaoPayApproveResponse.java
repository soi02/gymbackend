package com.ca.gymbackend.challenge.dto.payment;

import lombok.Data;

// 결제 승인 응답

@Data
public class KakaoPayApproveResponse {
    private String aid; // 요청 고유 번호
    private String tid; // 결제 고유 번호
    private String cid; // 가맹점 코드
    private String partner_order_id; // 가맹점 주문번호
    private String partner_user_id; // 가맹점 회원 ID
    private String item_name; // 상품 이름
    private String created_at; // 결제 준비 요청 시각
    private String approved_at; // 결제 승인 시각
}
