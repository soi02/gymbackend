package com.ca.gymbackend.challenge.dto.payment;

import lombok.Data;

// 프론트엔드 응답용
@Data
public class PaymentReadyResponse {
    private String tid; // 카카오페이로부터 받은 tid

    private String redirectUrl; // 결제 페이지 URL : PC 결제 리다이렉트 URL (QR 코드 포함)

    private String next_redirect_mobile_url; // 모바일 결제 리다이렉트 URL

}
