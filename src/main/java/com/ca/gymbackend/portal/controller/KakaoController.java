package com.ca.gymbackend.portal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
public class KakaoController {

    // private final String clientId = 

    // @GetMapping("/oauth2/kakao/login")
    // public void login(HttpServletResponse response) throws IOException {
    //     String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
    //         + "?response_type=code"
    //         + "&client_id=" + clientId
    //         + "&redirect_uri=" + redirectUri;
    //     response.sendRedirect(kakaoAuthUrl);
    // }
}
