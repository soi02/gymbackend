package com.ca.gymbackend.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.portal.request.LoginRequest;
import com.ca.gymbackend.portal.response.ApiResponse;
import com.ca.gymbackend.portal.response.LoginResponse;
import com.ca.gymbackend.portal.service.PortalService;
import com.ca.gymbackend.security.JwtUtil;

@RestController
@RequestMapping("/api/user")
public class PortalController {
    @Autowired
    private PortalService portalService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        UserDto user = portalService.findByAccountName(loginRequest.getAccountName(),loginRequest.getPassword());
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
        String token = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(new LoginResponse(true, token, user.getName()));
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(new ApiResponse(false, "Authorization 헤더가 없거나 형식이 올바르지 않습니다."));
            }
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);

            if (isValid) {
                Integer userId = jwtUtil.getUserId(token);
                UserDto user = portalService.findById(userId);

                return ResponseEntity.ok(new ApiResponse(true, user.getName()));
            } else {
                return ResponseEntity.status(401).body(new ApiResponse(false, "토큰이 유효하지 않습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "토큰 검증 중 오류가 발생했습니다."));
        }
    }
}
