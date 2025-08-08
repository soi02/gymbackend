package com.ca.gymbackend.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.challenge.service.TestChallengeServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/challenge/test")
@Profile("dev")
public class TestChallengeController {
    
   @Autowired
    private TestChallengeServiceImpl testChallengeService;

    @PostMapping("/setTestAttendance")
    public ResponseEntity<String> setTestAttendance(@RequestParam("userId") int userId,
                                                    @RequestParam("challengeId") int challengeId,
                                                    @RequestParam("count") int count) {
        try {
            testChallengeService.updateTestAttendanceCount(userId, challengeId, count);
            return ResponseEntity.ok("출석 횟수 설정 완료.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("출석 횟수 설정 실패: " + e.getMessage());
        }
    }
}