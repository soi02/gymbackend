package com.ca.gymbackend.portal.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.portal.request.LoginRequest;
import com.ca.gymbackend.portal.request.UserRequest;
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
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @ModelAttribute UserRequest userRequest, // JSON 대신 폼 데이터 바인딩
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile // 프로필 이미지 파일
                                                                                                       // (선택적)
    ) {
        UserDto userDto = new UserDto();
        userDto.setId(userRequest.getId());
        userDto.setName(userRequest.getName());
        // userDto.setAge(userRequest.getAge());
        userDto.setGender(userRequest.getGender());
        userDto.setAccountName(userRequest.getAccountName());
        userDto.setPassword(userRequest.getPassword());
        userDto.setBirth(userRequest.getBirth());
        userDto.setAddress(userRequest.getAddress());
        userDto.setPhone(userRequest.getPhone());
        // userDto.setProfileImage(userRequest.getProfileImage()); // 이 부분은 파일 업로드로
        // 대체됩니다.
        userDto.setHeight(userRequest.getHeight());
        userDto.setWeight(userRequest.getWeight());
        userDto.setMuscleMass(userRequest.getMuscleMass());
        userDto.setBuddy(userRequest.isBuddy());

        // 1. 프로필 이미지 파일이 존재하면 저장 처리
        try {
            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                // PortalService의 saveImage 메서드를 호출하여 파일 저장 및 경로를 받습니다.
                UserDto imageResultDto = portalService.saveImage(
                        profileImageFile.getBytes(),
                        profileImageFile.getOriginalFilename());
                // 저장된 이미지 경로를 userDto에 설정합니다.
                userDto.setProfileImage(imageResultDto.getProfileImage());
            } else {
                // 파일이 없는 경우, 기본 이미지 경로를 설정하거나 null로 둡니다.
                userDto.setProfileImage(null); // 또는 "/default/profile.png" 와 같은 기본 경로
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "프로필 이미지 업로드 실패: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "이미지 처리 중 오류 발생: " + e.getMessage()));
        }

        // 2. 사용자 정보(이미지 경로 포함) 등록
        portalService.register(userDto);
        System.out.println("------------------------------");
        System.out.println(profileImageFile.getOriginalFilename());

        return ResponseEntity.ok(new ApiResponse(true, "회원가입 성공"));
    }

    // 다른 엔드포인트들은 그대로 유지하거나 필요에 따라 추가합니다.

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        UserDto userDto = portalService.findByAccountName(loginRequest.getAccountName(), loginRequest.getPassword());
        if (userDto == null || !userDto.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
        String token = jwtUtil.generateToken(userDto.getId());

        return ResponseEntity.ok(new LoginResponse(true, token, userDto.getName(), userDto.getId()));
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
                UserDto userDto = portalService.findById(userId);

                return ResponseEntity.ok(new ApiResponse(true, userDto.getName(), userDto.getId()));
            } else {
                return ResponseEntity.status(401).body(new ApiResponse(false, "토큰이 유효하지 않습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "토큰 검증 중 오류가 발생했습니다."));
        }
    }
}
