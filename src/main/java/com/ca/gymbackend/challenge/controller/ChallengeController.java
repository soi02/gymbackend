package com.ca.gymbackend.challenge.controller;

import java.util.List;

import org.springframework.http.HttpStatus; // ★ 추가
import org.springframework.http.ResponseEntity; // ★ 추가
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.dto.ChallengeDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.dto.ChallengeProgressResponse;
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
    public ResponseEntity<String> registerChallengeProcess(
            @ModelAttribute ChallengeCreateRequest challengeCreateRequest,
            @RequestParam(value = "challengeKeywordNameList", required = false) List<String> challengeKeywordNameList,
            @RequestPart(value = "challengeThumbnailImage", required = false) MultipartFile challengeThumbnailImage) {
    
        System.out.println("컨트롤러 진입");
        System.out.println("받은 챌린지 데이터: " + challengeCreateRequest);
        System.out.println("받은 키워드: " + challengeKeywordNameList);
        System.out.println("받은 썸네일 파일 이름: " + (challengeThumbnailImage != null ? challengeThumbnailImage.getOriginalFilename() : "없음"));

        String creatorId = challengeCreateRequest.getChallengeCreator();
        if (creatorId == null || creatorId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 사용자만 챌린지를 생성할 수 있습니다.");
        }

        try {
            // 1. 이미지 저장
            if (challengeThumbnailImage != null && !challengeThumbnailImage.isEmpty()) {
                System.out.println("이미지 업로드 시작: " + challengeThumbnailImage.getOriginalFilename());
                String imagePath = challengeService.saveChallengeThumbnailImage(
                    challengeThumbnailImage.getBytes(),
                    challengeThumbnailImage.getOriginalFilename());

                if (imagePath == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장 중 오류 발생");
                }

                challengeCreateRequest.setChallengeThumbnailPath(imagePath);
            }

            // 2. 챌린지 정보 DB에 저장
            challengeService.saveChallengeData(challengeCreateRequest);

            // 3. 방금 생성된 챌린지 ID 가져오기
            int generatedChallengeId = challengeCreateRequest.getChallengeId();

            // 4. 챌린지 - 키워드 연결 매핑
            challengeService.saveChallengekeywordMapping(generatedChallengeId, challengeKeywordNameList);

            return ResponseEntity.status(HttpStatus.CREATED).body("챌린지 생성 완료");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("챌린지 생성 실패: " + e.getMessage());
        }
    }


    // 챌린지 리스트 가져오기
    @GetMapping("/getAllChallengeListProcess")
    public ResponseEntity<List<ChallengeCreateRequest>> getAllChallengeListProcess() {
        System.out.println("[챌린지 목록 응답]");
        List<ChallengeCreateRequest> challengeCreateRequestList = challengeService.getAllChallengeList();
        return ResponseEntity.ok(challengeCreateRequestList);
    }


    // 챌린지 상세보기
    @GetMapping("/getChallengeDetailByChallengeIdProcess")
    public ResponseEntity<ChallengeDetailResponse> getChallengeDetailByChallengeIdProcess(
        @RequestParam("challengeId") int challengeId,
        @RequestParam(value = "userId", required = false) Integer userId) {

        System.out.println(">>> getChallengeDetailByChallengeIdProcess 호출됨. challengeId: " + challengeId + ", userId: " + userId); 

        if (userId == null) {
            userId = 0;
        }

        ChallengeDetailResponse challengeDetailResponse = challengeService.getChallengeDetailByChallengeId(challengeId, userId);

        // 챌린지를 찾을 수 없으면 404 Not Found 반환
        if(challengeDetailResponse == null) {
            System.out.println("챌린지를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(challengeDetailResponse);
    }
    
    // 챌린지 도전 시작
    @PostMapping("/startChallengeProcess")
    public ResponseEntity<String> startChallengeProcess(
        @RequestBody ChallengeStartRequest challengeStartRequest) {
            if (challengeStartRequest.getUserId() <= 0 || challengeStartRequest.getChallengeId() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UserId와 ChallengeId는 유효해야 합니다.");
            }

            try {
                // 1. 중복 참여 여부 확인
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

                return ResponseEntity.ok("챌린지 도전 시작 성공");

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러: 챌린지 도전 시작 실패 " + e.getMessage());
            }
        }
        
    // 나의 수련기록 조회
    @GetMapping("/getAllMyChallengeListProcess")
    public ResponseEntity<List<ChallengeMyRecordsResponse>> getAllMyChallengeListProcess(@RequestParam("userId") int userId) {
        if (userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<ChallengeMyRecordsResponse> challengeMyRecordsResponseList = challengeService.getAllMyChallengeList(userId);
        
        // 챌린지 목록이 비어있더라도 200 OK를 반환하고 빈 리스트를 보냅니다.
        // 이는 정상적인 응답이며, 프론트엔드가 이를 처리해야 합니다.
        return ResponseEntity.ok(challengeMyRecordsResponseList);
    }

    // 특정 사용자의 특정 챌린지 상세 정보 & 인증 기록 조회
    @GetMapping("/getMyRecordDetailProcess")
    public ResponseEntity<ChallengeMyRecordDetailResponse> getMyRecordDetailProcess(
        @RequestParam("userId") int userId,
        @RequestParam("challengeId") int challengeId) {
            if (userId <= 0 || challengeId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            ChallengeMyRecordDetailResponse challengeMyRecordDetailResponse = challengeService.getMyRecordDetail(userId, challengeId);
            if (challengeMyRecordDetailResponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(challengeMyRecordDetailResponse);
    }








    // 새로운 API 1: 챌린지 상세 진행 상황 조회
    @GetMapping("/getChallengeProgressProcess")
    public ResponseEntity<ChallengeProgressResponse> getChallengeProgressProcess(
            @RequestParam("challengeId") int challengeId,
            @RequestParam("userId") int userId) {
        if (challengeId <= 0 || userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        ChallengeProgressResponse response = challengeService.getChallengeProgressInfo(challengeId, userId);
        
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        
        return ResponseEntity.ok(response);
    }

    // 새로운 API 2: 일일 인증 사진 업로드
    @PostMapping("/attendChallengeProcess")
    public ResponseEntity<String> attendChallengeProcess(
            @RequestParam("userId") int userId,
            @RequestParam("challengeId") int challengeId,
            @RequestPart("photo") MultipartFile photo) {
        
        if (userId <= 0 || challengeId <= 0 || photo == null || photo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("필수 요청 파라미터가 누락되었습니다.");
        }
        
        try {
            challengeService.attendChallenge(userId, challengeId, photo);
            return ResponseEntity.ok("챌린지 인증 완료!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 처리 중 오류 발생");
        }
    }








}