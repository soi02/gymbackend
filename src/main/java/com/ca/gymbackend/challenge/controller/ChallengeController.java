package com.ca.gymbackend.challenge.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus; // ★ 추가
import org.springframework.http.ResponseEntity; // ★ 추가
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.dto.ChallengeDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeFinalTestResult;
import com.ca.gymbackend.challenge.dto.ChallengeKeywordCategory;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.dto.ChallengeProgressResponse;
import com.ca.gymbackend.challenge.dto.ChallengeStartRequest;
import com.ca.gymbackend.challenge.dto.ChallengeTendencyTestRequest;
import com.ca.gymbackend.challenge.dto.payment.PaymentReadyResponse;
import com.ca.gymbackend.challenge.service.ChallengeServiceImpl;
import com.ca.gymbackend.challenge.service.PaymentServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
public class ChallengeController {
    
    private final ChallengeServiceImpl challengeService;
    private final PaymentServiceImpl paymentService;

    // 챌린지 생성
    @PostMapping("/registerChallengeProcess")
    public ResponseEntity<String> registerChallengeProcess(@ModelAttribute ChallengeCreateRequest challengeCreateRequest) {
        
    // ★★★ 이 부분을 추가해주세요 ★★★
    System.out.println("백엔드에서 수신한 챌린지 생성 요청 데이터: " + challengeCreateRequest);
    System.out.println("보증금: " + challengeCreateRequest.getChallengeDepositAmount());
    // ★★★ 여기까지 추가 ★★★

        // 로그인 사용자 확인 로직
        String creatorName = challengeCreateRequest.getChallengeCreator();
        if (creatorName == null || creatorName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 사용자만 챌린지를 생성할 수 있습니다.");
        }

        try {
            // 서비스 계층으로 모든 비즈니스 로직을 위임
            challengeService.registerChallenge(challengeCreateRequest);
            return ResponseEntity.ok("챌린지 생성 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("챌린지 생성 실패: \n\n" + e.getMessage());
        }
    }




    // 챌린지 리스트 가져오기
    @GetMapping("/getAllChallengeListProcess")
    public ResponseEntity<List<ChallengeCreateRequest>> getAllChallengeListProcess() {
        System.out.println("[챌린지 목록 응답]");
        List<ChallengeCreateRequest> challengeCreateRequestList = challengeService.getAllChallengeList();
        return ResponseEntity.ok(challengeCreateRequestList);
    }


    // 모든 키워드 카테고리 목록을 가져오는 API
    @GetMapping("/getAllCategories")
    public ResponseEntity<List<ChallengeKeywordCategory>> getAllCategories() {
        System.out.println("[모든 카테고리 목록 응답]");
        List<ChallengeKeywordCategory> categories = challengeService.getAllKeywordCategories();
        return ResponseEntity.ok(categories);
    }

    // 카테고리별 챌린지 목록 조회
    @GetMapping("/getChallengesByCategoryId/{categoryId}")
    public ResponseEntity<List<ChallengeCreateRequest>> getChallengesByCategoryId(@PathVariable("categoryId") Integer categoryId) {
        System.out.println("[카테고리별 챌린지 목록 조회] categoryId: " + categoryId);
        try {
            List<ChallengeCreateRequest> challenges = challengeService.getChallengesByCategoryId(categoryId);
            return ResponseEntity.ok(challenges);
        } catch (IllegalArgumentException e) {
            System.err.println("오류: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
            @RequestPart("photo") MultipartFile photo,
            @RequestParam(value = "testDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate testDate) {
        
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
    // try { (테스트용입니다)
    //     // testDate 파라미터를 서비스 메서드로 전달
    //     challengeService.attendChallenge(userId, challengeId, photo, testDate);
    //     return ResponseEntity.ok("챌린지 인증 완료!");
    // } catch (IllegalStateException e) {
    //     return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    // } catch (Exception e) {
    //     e.printStackTrace();
    //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 처리 중 오류 발생");
    // }
    }





    // 키워드에 따른 챌린지 추천
    @GetMapping("/getRecommendedChallengeListProcess")
    public ResponseEntity<List<ChallengeCreateRequest>> getRecommendedChallengeListProcess(
        @RequestParam("keywordIds") List<Integer> keywordIds
    ) {
        System.out.println("[추천 챌린지 조회] keywordIds: " + keywordIds);
        try {
            List<ChallengeCreateRequest> recommendations = challengeService.getRecommendedChallengeList(keywordIds);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }





    // 성향 테스트 결과 저장
    @PostMapping("/tendency-test/complete")
    public ResponseEntity<String> completeTendencyTest(@RequestBody ChallengeTendencyTestRequest request) {
        try {
            challengeService.tendencyTestComplete(request.getUserId(), request.getKeywordIds());
            return ResponseEntity.ok("성향 테스트 결과가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("성향 테스트 결과 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }



    // 사용자의 성향 테스트 완료 여부 확인 (바텀바에서 처음 수련장 탭했을 때 어디로 보낼지 결정)
    @GetMapping("/tendency-test/status")
    public ResponseEntity<Boolean> getTendencyTestStatus(@RequestParam("userId") int userId) {
            System.out.println("[DEBUG] getTendencyTestStatus 메서드 호출됨."); // ✅ 진입점 로그
    System.out.println("[DEBUG] 전달받은 userId: " + userId); // ✅ 파라미터 로그
        
    try {
        boolean hasCompleted = challengeService.hasUserCompletedTendencyTest(userId);
        System.out.println("[DEBUG] 성향 테스트 완료 여부: " + hasCompleted); // ✅ 서비스 결과 로그
        return ResponseEntity.ok(hasCompleted);
    } catch (Exception e) {
        System.out.println("[ERROR] getTendencyTestStatus 처리 중 에러 발생: " + e.getMessage()); // ✅ 예외 로그
        return ResponseEntity.status(500).build();
    }
    }


    // 사용자의 성향 테스트 결과를 조회 (나의 수련기록 페이지에 성향 테스트 결과를 보여주기 위해 사용)
    @GetMapping("/tendency-test/result")
    public ResponseEntity<ChallengeFinalTestResult> getTendencyTestResult(@RequestParam("userId") int userId) {
        ChallengeFinalTestResult result = challengeService.findTestResult(userId);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }





    

    // 결제 준비 API
    @PostMapping("/join/payment")
    public ResponseEntity<PaymentReadyResponse> startChallengeWithPayment(
            @RequestParam("userId") int userId,
            @RequestParam("challengeId") int challengeId) {
        try {
            // Service 계층으로 요청 위임
            PaymentReadyResponse response = challengeService.startChallengeWithPayment(userId, challengeId);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // 결제 승인 API
    @GetMapping("/join/payment/success")
    public ResponseEntity<String> kakaoPaySuccess(
            @RequestParam("pg_token") String pgToken,
            @RequestParam("challengeId") int challengeId,
            @RequestParam("userId") int userId) {
        try {
            // Service 계층으로 요청 위임
            boolean paymentSuccess = paymentService.kakaoPayApprove(Long.valueOf(challengeId), userId, pgToken);

            if (paymentSuccess) {
                // 결제 승인 성공 후 챌린지 참가 최종 처리
                challengeService.finalizeChallengeJoin(userId, challengeId);
                return ResponseEntity.ok("결제 및 챌린지 참가 성공");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 승인 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 승인 중 오류 발생: " + e.getMessage());
        }
    }

}