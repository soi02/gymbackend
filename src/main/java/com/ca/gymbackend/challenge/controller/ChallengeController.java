package com.ca.gymbackend.challenge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.dto.ChallengeDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.dto.ChallengeStartRequest;
import com.ca.gymbackend.challenge.service.ChallengeServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
public class ChallengeController {
    
    private final ChallengeServiceImpl challengeService;

    // 챌린지 생성
    @PostMapping("/registerChallengeProcess")
    public String registerChallengeProcess(
            @ModelAttribute ChallengeCreateRequest challengeCreateRequest,
            @RequestParam(value = "challengeKeywordNameList", required = false) List<String> challengeKeywordNameList,
            @RequestPart(value = "challengeThumbnailImage", required = false) MultipartFile challengeThumbnailImage) {
    
    System.out.println("컨트롤러 진입");
    System.out.println("받은 챌린지 데이터: " + challengeCreateRequest);
    System.out.println("받은 키워드: " + challengeKeywordNameList);
    System.out.println("받은 썸네일 파일 이름: " + (challengeThumbnailImage != null ? challengeThumbnailImage.getOriginalFilename() : "없음"));

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

        // 2. 챌린지 정보 DB에 저장
        challengeService.saveChallengeData(challengeCreateRequest);

        // 3. 방금 생성된 챌린지 ID 가져오기
        int generatedChallengeId = challengeCreateRequest.getChallengeId();

        // 4. 챌린지 - 키워드 연결 매핑 (챌린지 ID 조회 후 키워드 이름 목록 매핑)
        challengeService.saveChallengekeywordMapping(generatedChallengeId, challengeKeywordNameList);


        return "챌린지 생성 완료";

    } catch (Exception e) {
        e.printStackTrace();
        return "챌린지 생성 실패: " + e.getMessage();
    }
}


    // 챌린지 리스트 가져오기
    @GetMapping("/getAllChallengeListProcess")
    public List<ChallengeCreateRequest> getAllChallengeListProcess() {
        System.out.println("[챌린지 목록 응답]");
        return challengeService.getAllChallengeList();
    }




    // 챌린지 상세보기
    @GetMapping("/getChallengeDetailByChallengeIdProcess")
    public ChallengeDetailResponse getChallengeDetailByChallengeIdProcess(
        @RequestParam("challengeId") int challengeId,
        @RequestParam(value = "userId", required = false) Integer userId) {

        System.out.println(">>> getChallengeDetailByChallengeIdProcess 호출됨. challengeId: " + challengeId + ", userId: " + userId); 

        // userId가 null일 경우 0으로 설정하여, 비로그인 상태임을 서비스에 전달
        if (userId == null) {
            userId = 0;
        }

        ChallengeDetailResponse challengeDetailResponse = challengeService.getChallengeDetailByChallengeId(challengeId, userId);

        if(challengeDetailResponse == null) {
            System.out.println("챌린지를 찾을 수 없습니다.");
            return null;
        }

        return challengeDetailResponse;
    }
    




    // 챌린지 도전 시작
    @PostMapping("/startChallengeProcess")
    public String startChallengeProcess(
        @RequestBody ChallengeStartRequest challengeStartRequest) {
            if (challengeStartRequest.getUserId() == 0 || challengeStartRequest.getChallengeId() == 0) {
                return "에러: UserId와 ChallengeId 는 null 이 아니어야 합니다.";
            }

            try {

                // 1. 중복 참여 여부 확인 (서비스 메서드 분리)
                challengeService.checkExistsUserChallenge(
                    challengeStartRequest.getUserId(), 
                    challengeStartRequest.getChallengeId()
                );

                // 2. user_challenge 테이블에 사용자 챌린지 정보를 삽입
                challengeService.insertUserChallengeInfo(
                    challengeStartRequest.getUserId(), 
                    challengeStartRequest.getChallengeId()
                );

                // 3. challenge 테이블의 participant_count를 1 증가시키기
                challengeService.increaseChallengeParticipantCountInfo(
                    challengeStartRequest.getChallengeId()
                );

                return "챌린지 도전 시작 성공";

            } catch (Exception e) {
                e.printStackTrace();
                return "에러: 챌린지 도전 시작 실패 " + e.getMessage();
            }
        }
        
    



        // 나의 수련기록 조회
        @GetMapping("/getAllMyChallengeListProcess")
        public List<ChallengeMyRecordsResponse> getAllMyChallengeListProcess(@RequestParam("userId") int userId) {
            return challengeService.getAllMyChallengeList(userId);
        }



        // 특정 사용자의 특정 챌린지 상세 정보 & 인증 기록 조회
        @GetMapping("/getMyRecordDetailProcess")
        public ChallengeMyRecordDetailResponse getMyRecordDetailProcess(
            @RequestParam("userId") int userId,
            @RequestParam("challengeId") int challengeId) {
                return challengeService.getMyRecordDetail(userId, challengeId);
            }
        

}
