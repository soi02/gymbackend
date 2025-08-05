package com.ca.gymbackend.challenge.dto.payment;
// 결제 승인 요청

import lombok.Data;

@Data
public class PaymentApproveRequest {
    private String tid;
    private int challengeId;
    private int userId;
    private String pg_token;
}
