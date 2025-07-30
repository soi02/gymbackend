package com.ca.gymbackend.buddy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.buddy.dto.BuddyDto;
import com.ca.gymbackend.buddy.service.BuddyServiceImpl;
import com.ca.gymbackend.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/buddy")
public class BuddyController {

    @Autowired
    private BuddyServiceImpl buddyService;
    @Autowired
    private JwtUtil jwtUtil;

    // @PostMapping("/register")
    // public String registerBuddy(@RequestBody BuddyDto buddyDto) {
    // buddyService.registerBuddy(buddyDto);
    // System.out.println("넘어온 buddyDto: " + buddyDto);
    // System.out.println("buddyAgeList: " + buddyDto.getBuddyAgeList());
    // return "버디 등록 완료";
    // }
    @PostMapping("/register")
    public String registerBuddy(@RequestBody BuddyDto buddyDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // "Bearer " 제거
        Integer userId = jwtUtil.getUserId(token); // JwtUtil에 이미 존재함

        System.out.println("Before set: " + buddyDto.getUserId());
        buddyDto.setUserId(userId);
        System.out.println("After set: " + buddyDto.getUserId());

        buddyService.registerBuddy(buddyDto);
        buddyService.updateIsBuddy(buddyDto.getUserId());

        // System.out.println("넘어온 buddyDto: " + buddyDto);
        // System.out.println("buddyAgeList: " + buddyDto.getBuddyAgeList());
        return "버디 등록 완료";
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getBuddyUserList() {
        List<Map<String, Object>> result = buddyService.getBuddyUserList();
        return ResponseEntity.ok(result);
    }
}
