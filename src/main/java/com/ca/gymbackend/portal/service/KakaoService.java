package com.ca.gymbackend.portal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoService {
    @Value("${kakao.auth.url}")
    private String KAKAO_URL;

    @Value("${kakao.auth.client.id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect.url}")
    private String KAKAO_REDIRECT_URL;

    /**
     * 카카오 로그인 하기전 인증 URL 요청
     * @return
     */
    public String getAuthUrl(){
        return UriComponentsBuilder.fromHttpUrl(KAKAO_URL)
                .queryParam("client_id", KAKAO_CLIENT_ID)
                .queryParam("redirect_uri", KAKAO_REDIRECT_URL)
                .queryParam("response_type", "code")
                .build()
                .toUriString();
    }
}
