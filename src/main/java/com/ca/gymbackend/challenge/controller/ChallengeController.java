package com.ca.gymbackend.challenge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.service.ChallengeServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challengeList")
public class ChallengeController {
    
    private final ChallengeServiceImpl challengeService;

    // 챌린지 생성
@PostMapping("/registerChallengeProcess")
public String registerChallenge(
        @ModelAttribute ChallengeCreateRequest challengeRequest,
        @RequestParam("challengeImage") MultipartFile imageFile) {

    try {
        challengeService.registerChallenge(challengeRequest, imageFile);
        return "챌린지 생성 완료";
    } catch (Exception e) {
        e.printStackTrace();
        return "챌린지 생성 실패";
    }
}


    // 챌린지 리스트 가져오기
    @GetMapping("getChallengeList")
    public List<ChallengeCreateRequest> getAllChallengeListProcess() {
        System.out.println("🔥 [챌린지 목록 응답] 🔥");
        return challengeService.getAllChallengeList();
    }



}
