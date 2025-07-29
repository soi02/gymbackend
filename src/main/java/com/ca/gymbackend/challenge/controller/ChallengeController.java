package com.ca.gymbackend.challenge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.service.ChallengeServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challengeList")
public class ChallengeController {
    
    private final ChallengeServiceImpl challengeService;

    // 챌린지 생성
    @PostMapping("createChallenge")
    public String registerChallengeProcess(@RequestBody ChallengeCreateRequest challengeCreateRequest) {
        challengeService.registerChallenge(challengeCreateRequest);
        return "챌린지 생성 완료";
    }

    // 챌린지 리스트 가져오기
    @GetMapping("getChallengeList")
    public List<ChallengeCreateRequest> getAllChallengeListProcess() {
        return challengeService.getAllChallengeList();
    }


}
