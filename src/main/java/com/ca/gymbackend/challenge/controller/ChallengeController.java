package com.ca.gymbackend.challenge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
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
    public String registerChallengeProcess(
            @ModelAttribute ChallengeCreateRequest challengeCreateRequest,
            @RequestParam(value = "challengeKeywordIds", required = false) List<Integer> challengeKeywordIds,
            @RequestPart(value = "challengeThumbnailImage", required = false) MultipartFile challengeThumbnailImage) {
    
    System.out.println("컨트롤러 진입");

    if (challengeCreateRequest.getChallengeCreator() == null) {
        return "로그인 사용자만 챌린지를 생성할 수 있습니다.";
    }


    try {
        // 1. 이미지 저장
        if (challengeThumbnailImage != null && !challengeThumbnailImage.isEmpty()) {
            System.out.println("이미지 업로드 시작: " + challengeThumbnailImage.getOriginalFilename());
            String imagePath = challengeService.saveChallengeThumbnailImage(
                challengeThumbnailImage.getBytes(),
                challengeThumbnailImage.getOriginalFilename());

            if (imagePath == null) {
                return "이미지 저장 중 오류 발생";
            }

            challengeCreateRequest.setChallengeThumbnailPath(imagePath);
        }

        // 2. 키워드 별도 바인딩
        challengeCreateRequest.setChallengeKeywordIds(challengeKeywordIds);

        // 3. 챌린지 저장
        challengeService.saveChallengeData(challengeCreateRequest);

        // 4. ID 조회 후 키워드 매핑
        int challengeId = challengeService.getGeneratedChallengeId();
        challengeService.saveChallengekeywordMapping(challengeId, challengeKeywordIds);

        return "챌린지 생성 완료";

    } catch (Exception e) {
        e.printStackTrace();
        return "챌린지 생성 실패: " + e.getMessage();
    }
}


    // 챌린지 리스트 가져오기
    @GetMapping("getChallengeList")
    public List<ChallengeCreateRequest> getAllChallengeListProcess() {
        System.out.println("[챌린지 목록 응답]");
        return challengeService.getAllChallengeList();
    }




    // 챌린지 상세보기
    



}
