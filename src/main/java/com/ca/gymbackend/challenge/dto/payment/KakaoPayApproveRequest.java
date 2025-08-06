package com.ca.gymbackend.challenge.dto.payment;

import lombok.Data;

// 결제 승인 요청

@Data
public class KakaoPayApproveRequest {
    private String cid;
    private String tid; // 결제 준비 응답에서 받은 tid
    private String partner_order_id;
    private String partner_user_id;
    private String pg_token; // 결제 성공 리다이렉트 시 받은 pg_token
}
