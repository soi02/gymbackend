package com.ca.gymbackend.challenge.dto.payment;

import lombok.Data;

// 결제 준비 응답 

@Data
public class KakaoPayReadyResponse {
    private String tid; // 결제 고유 번호, 20자
    private String next_redirect_mobile_url; // 모바일 웹용 결제 페이지 URL
    private String next_redirect_pc_url; // PC 웹용 결제 페이지 URL
    private String created_at; // 결제 준비 요청 시간
}
