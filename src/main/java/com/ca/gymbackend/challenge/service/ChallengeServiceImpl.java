package com.ca.gymbackend.challenge.service;

import com.ca.gymbackend.challenge.dto.ChallengeAttendanceRecord;
import com.ca.gymbackend.challenge.dto.ChallengeAttendanceStatus;
import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.dto.ChallengeDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeFinalTestResult;
import com.ca.gymbackend.challenge.dto.ChallengeInfo;
import com.ca.gymbackend.challenge.dto.ChallengeKeywordCategory;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.dto.ChallengeProgressResponse;
import com.ca.gymbackend.challenge.dto.ChallengeRecordInfo;
import com.ca.gymbackend.challenge.dto.ChallengeTestScore;
import com.ca.gymbackend.challenge.dto.ChallengeUserInfo;
import com.ca.gymbackend.challenge.dto.payment.ChallengeRaffleTicket;
import com.ca.gymbackend.challenge.dto.payment.PaymentReadyResponse;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl {

    private final ChallengeMapper challengeMapper;
    private final PaymentServiceImpl paymentService;

    @Autowired
    @Qualifier("fileRootPath")
    private String rootPath;


    @Transactional
    public void registerChallenge(ChallengeCreateRequest challengeCreateRequest) {
        
        // 1. 챌린지 썸네일 이미지 저장
        String imagePath = null;
        MultipartFile thumbnailImage = challengeCreateRequest.getChallengeThumbnailImage();

        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            try {
                imagePath = saveChallengeThumbnailImage(thumbnailImage.getBytes(), thumbnailImage.getOriginalFilename());
            } catch (IOException e) {
                // 예외 처리
                throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
            }
        }

        // 사용자가 선택한 키워드 ID 목록을 기반으로 챌린지 성향 ID를 분류
        List<Integer> selectedKeywordIds = challengeCreateRequest.getChallengeKeywordIds();
        Integer predictedTendencyId = classifyTendency(selectedKeywordIds);
        challengeCreateRequest.setChallengeTendencyId(predictedTendencyId);
        System.out.println("DEBUG: 챌린지 성향 ID 자동 분류 완료 - " + predictedTendencyId);
        

        // 2. 챌린지 정보 DB에 저장
        challengeCreateRequest.setChallengeThumbnailPath(imagePath);
        challengeMapper.createChallenge(challengeCreateRequest);

        // 3. 챌린지-키워드 매핑
        int generatedChallengeId = challengeCreateRequest.getChallengeId();
        if (selectedKeywordIds != null && !selectedKeywordIds.isEmpty()) {
            for (Integer keywordId : selectedKeywordIds) {
                if (keywordId != null) {
                    challengeMapper.createChallengeKeyword(generatedChallengeId, keywordId);
                }
            }
        }
    }

    // 사용자가 선택한 키워드 ID를 기반으로 챌린지 성향 ID를 분류
    private Integer classifyTendency(List<Integer> selectedKeywordIds) {
        if (selectedKeywordIds == null || selectedKeywordIds.isEmpty()) {
            return 5; // 선택된 키워드가 없으면 균형형으로 분류
        }

        // 각 성향별 키워드 ID 매핑 정의
        // 이 부분을 DB에서 가져와 캐싱하는 방식으로 구현하면 더 좋습니다.
        Map<String, List<Integer>> tendencyKeywordMap = new HashMap<>();
        tendencyKeywordMap.put("goal", List.of(1, 2, 3, 4, 5, 23, 24, 25, 26, 27, 28, 29, 30));
        tendencyKeywordMap.put("relationship", List.of(11, 12, 13, 14, 15, 34, 35, 36, 37));
        tendencyKeywordMap.put("recovery", List.of(6, 7, 8, 9, 10));
        tendencyKeywordMap.put("learning", List.of(16, 17, 18, 19, 20, 31, 32));
        tendencyKeywordMap.put("habit", List.of(21, 22, 23, 24, 25)); // 키워드 DB에 '습관'은 21~24, 동기부여와 자기관리는 25~30
        
        // 키워드 카테고리 정보가 없는 경우를 대비하여 ID 직접 매핑 (제공된 키워드 정보 기반)
        Map<String, List<Integer>> tendencyMap = new HashMap<>();
        tendencyMap.put("goal", List.of(1, 2, 3, 4, 5, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35));
        tendencyMap.put("relationship", List.of(11, 12, 13, 14, 15, 38, 39, 40, 41, 42));
        tendencyMap.put("recovery", List.of(6, 7, 8, 9, 10));
        tendencyMap.put("learning", List.of(16, 17, 18, 19, 20, 31, 32, 33, 34, 35));
        tendencyMap.put("habit", List.of(21, 22, 23, 24));


        Map<String, Integer> keywordCounts = new HashMap<>();
        tendencyMap.keySet().forEach(key -> keywordCounts.put(key, 0));

        // 선택된 키워드 ID를 순회하며 점수 계산
        for (Integer keywordId : selectedKeywordIds) {
            for (Map.Entry<String, List<Integer>> entry : tendencyMap.entrySet()) {
                if (entry.getValue().contains(keywordId)) {
                    keywordCounts.put(entry.getKey(), keywordCounts.get(entry.getKey()) + 1);
                }
            }
        }
        
        String topTendency = "balanced";
        int maxCount = 0;
        
        for (Map.Entry<String, Integer> entry : keywordCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                topTendency = entry.getKey();
            }
        }
        
        if (maxCount == 0) {
            return 5; // 균형형 ID
        } else if ("goal".equals(topTendency)) {
            return 1; // 목표지향형 ID
        } else if ("relationship".equals(topTendency)) {
            return 2; // 관계지향형 ID
        } else if ("recovery".equals(topTendency)) {
            return 3; // 회복지향형 ID
        } else if ("learning".equals(topTendency)) {
            return 4; // 학습지향형 ID
        } else {
            return 5; // 균형형 ID
        }
    }






    public String saveChallengeThumbnailImage(byte[] buffer, String originalFilename) {
        try {
            String uuid = UUID.randomUUID().toString();
            long currentTime = System.currentTimeMillis();

            String filename = uuid + "_" + currentTime;
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            filename += ext;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
            String todayPath = simpleDateFormat.format(new Date(currentTime));
            Path dirPath = Paths.get(rootPath, "challengeImages", todayPath);
            Files.createDirectories(dirPath);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
            Path filePath = dirPath.resolve(filename);
            System.out.println("✅ 저장 경로: " + filePath.toString());

            // 썸네일 라이브러리 사용
            Thumbnails.of(inputStream)
                    .scale(1.0)
                    .toFile(filePath.toFile());

            // DB에 저장할 경로 반환
            return "/challengeImages/" + todayPath + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    // 챌린지 전체 목록 조회
    public List<ChallengeCreateRequest> getAllChallengeList() {
        return challengeMapper.findAllChallengeList();
    }


    public List<ChallengeKeywordCategory> getAllKeywordCategories() {
        return challengeMapper.findAllKeywordCategories();
    }

    // 카테고리 ID로 챌린지 목록 조회
    public List<ChallengeCreateRequest> getChallengesByCategoryId(Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 ID입니다.");
        }
        
        // 1. 카테고리별 챌린지 목록 조회 (키워드 정보는 없는 상태)
        List<ChallengeCreateRequest> challenges = challengeMapper.findChallengesByCategoryId(categoryId);
        
        // 2. 각 챌린지에 대해 키워드 ID 목록을 조회하여 DTO에 설정
        for (ChallengeCreateRequest challenge : challenges) {
            List<Integer> keywordIds = challengeMapper.findKeywordIdsByChallengeId(challenge.getChallengeId());
            challenge.setChallengeKeywordIds(keywordIds);
        }
        
        return challenges;
    }






    // 챌린지 상세보기
    public ChallengeDetailResponse getChallengeDetailByChallengeId(int challengeId, Integer userId) {

        ChallengeDetailResponse challengeDetailResponse = challengeMapper.findChallengeDetailByChallengeId(challengeId);

        if (challengeDetailResponse == null) {
            // 키워드가 없는 챌린지라면 null 반환 예외처리
            return null;
        }

        // 서비스 계층에서 수동으로 데이터 가공 (키워드만
        // challengeKeywordsString (String)을 challengeKeywords List<String>타입으로 변환
        // INNER JOIN을 사용했으므로 이 keywordsString은 보통 NULL이 아니겠지만,
        // 혹시 모를 상황(예: GROUP_CONCAT이 빈 문자열 반환)을 대비하여 NULL/빈 문자열 체크는 유지
        String challengeKeywordsString = challengeDetailResponse.getChallengeKeywordsString();
        if (challengeKeywordsString != null && !challengeKeywordsString.trim().isEmpty()) {
            List<String> keywords = Arrays.asList(challengeKeywordsString.split(","))
                                            .stream()
                                            .map(String::trim)
                                            .collect(Collectors.toList());
            challengeDetailResponse.setChallengeKeywords(keywords);
        } else {
            // INNER JOIN으로 왔는데도 여기가 실행된다면, 논리적으로 키워드는 있었지만 GROUP_CONCAT이 빈 문자열을 반환한 경우입니다.
            challengeDetailResponse.setChallengeKeywords(new ArrayList<>());
        }
        // challengeKeywordsString 필드는 클라이언트에 불필요하므로 null로 설정
        challengeDetailResponse.setChallengeKeywordsString(null);

        // challengeStatus는 이미 SQL 쿼리에서 계산되어 들어왔으므로 별도 로직이 필요 없습니다.

        // 추가된 로직: userId를 사용하여 참여 여부 확인
        // Mapper에 existsUserChallenge(int userId, int challengeId) 메서드가 필요
        boolean isParticipating = challengeMapper.existsUserChallenge(userId, challengeId) > 0;
        challengeDetailResponse.setUserParticipating(isParticipating);

        return challengeDetailResponse;
    }






    // 챌린지 도전 시작
    // user_challenge 테이블에 이미 해당사용자와 챌린지Id 의 조합이 존재하는지 확인
    public void checkExistsUserChallenge (int userId, int challengeId) {
        int existingCount = challengeMapper.existsUserChallenge(userId, challengeId);
        if(existingCount > 0) {
            throw new IllegalStateException("이미 참여 중인 챌린지입니다.");
        }
    }

    // 1. user_challenge 테이블에 사용자 챌린지 정보를 삽입
    public void insertUserChallengeInfo(int userId, int challengeId) {
        challengeMapper.insertUserChallenge(userId, challengeId);
    }

    // 2. challenge 테이블의 participant_count를 1 증가시키기
    public void increaseChallengeParticipantCountInfo(int challengeId){
        challengeMapper.increaseChallengeParticipantCount(challengeId);
    }







    // 나의 수련기록
    // 내가 참여한 챌린지 목록 조회

    public List<ChallengeMyRecordsResponse> getAllMyChallengeList(int userId) {
        
        // 1. 참여한 챌린지 목록의 기본정보 조회
        List<ChallengeMyRecordsResponse> myChallengeList = challengeMapper.findAllMyChallengeList(userId);

        // 2. 스트림을 사용하여 각 챌린지에 대한 추가 출석정보를 조회하고 DTO에 설정
        return myChallengeList.stream().map(challenge -> {
            int challengeId = challenge.getChallengeId(); // 현재 처리중인 챌린지에서 챌린지 ID 가져옴

            // 챌린지별 총 출석일수 조회
            int daysAttended = challengeMapper.countAttendanceDays(userId, challengeId); // 출석일수 매퍼 통해서 데베에서 조회
            challenge.setDaysAttended(daysAttended);

            // 챌린지별 오늘 출석여부 조회
            // 0이면 false, 1 이상이면 true
            boolean todayAttended = challengeMapper.hasAttendedToday(userId, challengeId) > 0; // 출석여부 매퍼 통해서 데베에서 조회
            challenge.setTodayAttended(todayAttended);

            return challenge; // 출석일수, 출석여부 정보 추가된 객체임
        }).collect(Collectors.toList()); // challenge 객체를 List 형태로 다시 묶어준다        
    }





    // 특정 사용자의 특정 챌린지 상세 정보 & 인증 기록 조회
    public ChallengeMyRecordDetailResponse getMyRecordDetail(int userId, int challengeId) {

        // 챌린지 기본 정보 조회
        ChallengeInfo challengeInfo = challengeMapper.findChallengeInfoByChallengeId(challengeId);

        // 챌린지 정보가 없으면 예외 던짐
        if (challengeInfo == null) {
            throw new IllegalArgumentException("challengeId에 해당하는 챌린지 정보가 없습니다: " + challengeId);
        }

        // 사용자의 챌린지 인증 기록 조회
        List<ChallengeRecordInfo> challengeRecordInfoList = challengeMapper.findChallengeRecordList(userId, challengeId);

        // 인증 횟수 계산
        int daysAttended = challengeRecordInfoList.size();

        // DTO에 정확한 인증 횟수 설정
        challengeInfo.setDaysAttended(daysAttended);

        // 최종 응답 DTO 조립
        return new ChallengeMyRecordDetailResponse(challengeInfo, challengeRecordInfoList);
    }






    // 노리개
    // 챌린지 상세 진행 상황(스티커판)을 조회하는 메서드
    public ChallengeProgressResponse getChallengeProgressInfo(int challengeId, int userId) {
        // 1. 챌린지 기본 정보와 노리개 등급 정보 조회
        ChallengeProgressResponse response = challengeMapper.findChallengeProgressInfo(challengeId, userId);

        if (response == null) {
            // 사용자가 이 챌린지에 참여하지 않았거나, 챌린지 ID가 유효하지 않은 경우
            return null;
        }

        // 2. 총 달성 일수 계산
        int myAchievement = challengeMapper.countAttendedDays(challengeId, userId);
        response.setMyAchievement(myAchievement);

        // 3. 사용자의 출석 기록 조회
        List<ChallengeAttendanceRecord> records = challengeMapper.findAttendanceRecords(challengeId, userId);

        // 4. 스티커판 상태 리스트 생성 (기존 로직과 동일)
        ChallengeUserInfo challengeUserInfo = challengeMapper.findUserChallengeInfoByUserIdAndChallengeId(userId, challengeId);
        if (challengeUserInfo == null) {
            return null; // 사용자가 챌린지에 참여하지 않은 경우
        }
        LocalDate startDate = challengeUserInfo.getPersonalJoinDate().toLocalDate();
        LocalDate endDate = challengeUserInfo.getPersonalEndDate().toLocalDate();
        
        List<ChallengeAttendanceStatus> statusList = new ArrayList<>();
        Map<LocalDate, String> attendedDates = records.stream()
                .collect(Collectors.toMap(ChallengeAttendanceRecord::getAttendanceDate, ChallengeAttendanceRecord::getAttendanceImagePath));

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            ChallengeAttendanceStatus status = new ChallengeAttendanceStatus();
            status.setRecordDate(currentDate);

            if (currentDate.isAfter(LocalDate.now())) {
                status.setStatus("미래");
            } else if (attendedDates.containsKey(currentDate)) {
                status.setStatus("인증완료");
                status.setPhotoUrl(attendedDates.get(currentDate));
            } else {
                status.setStatus("결석");
            }
            statusList.add(status);
            currentDate = currentDate.plusDays(1);
        }
        
        response.setChallengeAttendanceStatus(statusList);
        return response;
    }


    // 일일 인증 사진을 로컬에 저장하고 URL을 반환
    private String saveAttendancePhoto(MultipartFile photo) throws IOException {
        String uuid = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();

        String originalFilename = photo.getOriginalFilename();
        String filename = uuid + "_" + currentTime;
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        filename += ext;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
        String todayPath = simpleDateFormat.format(new Date(currentTime));
        Path dirPath = Paths.get(rootPath, "attendancePhotos", todayPath); // 'attendancePhotos' 폴더에 저장
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(filename);
        photo.transferTo(filePath); // MultipartFile을 직접 파일로 저장

        return "/attendancePhotos/" + todayPath + filename;
    }



    // 일일 인증 기록을 저장하는 메서드 (추첨권 로직 수정)
    @Transactional
    public void attendChallenge(int userId, int challengeId, MultipartFile photo) {
        LocalDate today = LocalDate.now();

        try {
            int existingRecordCount = challengeMapper.countTodayAttendance(userId, challengeId, today);
            if (existingRecordCount > 0) {
                throw new IllegalStateException("오늘 이미 인증했습니다.");
            }

            String photoUrl = saveAttendancePhoto(photo);
            challengeMapper.insertAttendanceRecord(userId, challengeId, today, photoUrl);

            // 노리개 지급 및 추첨권 지급 로직
            int totalAttendedDays = challengeMapper.countAttendedDays(challengeId, userId);
            int totalChallengeDays = challengeMapper.findChallengeTotalDays(challengeId);

            if (totalChallengeDays > 0) {
                double achievementRate = (double) totalAttendedDays / totalChallengeDays * 100;
                int intAchievementRate = (int) achievementRate;
                
                // 노리개 등급별 획득 조건 및 추첨권 지급
                checkAndAwardNorigaeAndRaffleTicket(userId, challengeId, intAchievementRate, 3, 100); // Gold (100%)
                checkAndAwardNorigaeAndRaffleTicket(userId, challengeId, intAchievementRate, 2, 80);  // Silver (80%)
                checkAndAwardNorigaeAndRaffleTicket(userId, challengeId, intAchievementRate, 1, 50);  // Bronze (50%)
            }

        } catch (IOException e) {
            throw new RuntimeException("사진 파일 업로드 실패", e);
        }
    }

    // 노리개 등급 및 추첨권 지급을 확인하고 처리하는 보조 메서드
    private void checkAndAwardNorigaeAndRaffleTicket(int userId, int challengeId, int achievementRate, int tierId, int requiredRate) {
        if (achievementRate >= requiredRate) {
            if (challengeMapper.hasAwardedNorigae(userId, challengeId, tierId) == 0) {
                // 아직 해당 등급의 노리개를 획득하지 않았다면 지급
                challengeMapper.insertUserNorigae(userId, challengeId, tierId);

                // 노리개 획득 시 추첨권 1장 지급
                Integer currentTickets = challengeMapper.findUserRaffleTicketCount(userId);
                if (currentTickets == null) {
                    challengeMapper.insertUserRaffleTicket(userId, 1);
                } else {
                    challengeMapper.updateUserRaffleTicket(userId, currentTickets + 1);
                }
                System.out.println("INFO: 노리개 등급 " + tierId + " 획득 및 추첨권 1장 지급!");
            }
        }
    }






    // 키워드에 따른 챌린지 추천
    public List<ChallengeCreateRequest> getRecommendedChallengeList(List<Integer> keywordIds) {
        return challengeMapper.findRecommendedChallengeList(keywordIds);
    }






    // 성향 테스트 결과 저장
    @Transactional
    public void tendencyTestComplete(Integer userId, List<Integer> selectedKeywordIds) {
        try {
            System.out.println("tendencyTestComplete 메서드 시작. userId: " + userId);

            // 1. 키워드 ID 리스트를 기반으로 각 성향별 점수를 계산합니다.
            Map<String, Integer> scores = calculateTendencyScores(selectedKeywordIds);
            System.out.println("계산된 성향 점수: " + scores);

            // 2. test_score 테이블에 점수들을 저장합니다.
            ChallengeTestScore testScoreDto = new ChallengeTestScore();
            testScoreDto.setUserId(userId);
            testScoreDto.setGoalOriented(scores.getOrDefault("goal", 0));
            testScoreDto.setRelationshipOriented(scores.getOrDefault("relationship", 0));
            testScoreDto.setRecoveryOriented(scores.getOrDefault("recovery", 0));
            testScoreDto.setLearningOriented(scores.getOrDefault("learning", 0));
            testScoreDto.setBalanced(scores.getOrDefault("habit", 0));
            
            System.out.println("test_score 저장 시도...");
            challengeMapper.insertTestScore(testScoreDto);
            System.out.println("test_score 저장 성공. 생성된 test_score_id: " + testScoreDto.getTestScoreId());

            // 3. 점수 맵을 분석하여 최종 성향 결과를 결정합니다.
            List<Map.Entry<String, Integer>> sortedScores = scores.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            String topType1 = "balanced";
            Integer topScore1 = 0;
            String topType2 = "balanced";
            Integer topScore2 = 0;
            String testText = "균형 잡힌 성향입니다.";

            if (!sortedScores.isEmpty()) {
                topType1 = sortedScores.get(0).getKey();
                topScore1 = sortedScores.get(0).getValue();
                if (sortedScores.size() > 1) {
                    topType2 = sortedScores.get(1).getKey();
                    topScore2 = sortedScores.get(1).getValue();
                }
                testText = getTestResultText(topType1);
            }
            System.out.println("최종 성향 결과: top1=" + topType1 + ", top2=" + topType2);

            // 4. final_test_result 테이블에 최종 결과를 저장합니다.
            ChallengeFinalTestResult finalResultDto = new ChallengeFinalTestResult();
            finalResultDto.setUserId(userId);
            finalResultDto.setTestScoreId(testScoreDto.getTestScoreId());
            finalResultDto.setTopType1(topType1);
            finalResultDto.setTopScore1(topScore1);
            finalResultDto.setTopType2(topType2);
            finalResultDto.setTopScore2(topScore2);
            finalResultDto.setTestText(testText);
            
            System.out.println("final_test_result 저장 시도...");
            challengeMapper.insertFinalTestResult(finalResultDto);
            System.out.println("final_test_result 저장 성공.");
            
        } catch (Exception e) {
            System.err.println("성향 테스트 결과 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("성향 테스트 결과 저장 중 오류가 발생했습니다.", e);
        }
    }
    
    // 이전에 생성했던 calculateTendencyScores, getTestResultText, hasUserCompletedTendencyTest 메서드는 동일
    private Map<String, Integer> calculateTendencyScores(List<Integer> selectedKeywordIds) {
        Map<String, List<Integer>> tendencyMap = new HashMap<>();
        tendencyMap.put("goal", List.of(1, 2, 3, 4, 5, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35));
        tendencyMap.put("relationship", List.of(11, 12, 13, 14, 15, 38, 39, 40, 41, 42));
        tendencyMap.put("recovery", List.of(6, 7, 8, 9, 10));
        tendencyMap.put("learning", List.of(16, 17, 18, 19, 20, 31, 32, 33, 34, 35));
        tendencyMap.put("habit", List.of(21, 22, 23, 24));
        
        Map<String, Integer> scores = new HashMap<>();
        tendencyMap.keySet().forEach(key -> scores.put(key, 0));

        for (Integer keywordId : selectedKeywordIds) {
            for (Map.Entry<String, List<Integer>> entry : tendencyMap.entrySet()) {
                if (entry.getValue().contains(keywordId)) {
                    scores.put(entry.getKey(), scores.get(entry.getKey()) + 1);
                }
            }
        }
        return scores;
    }

    private String getTestResultText(String topType) {
        switch (topType) {
            case "goal":
                return "당신은 목표지향형입니다. 뚜렷한 목표를 향해 나아가는 것을 좋아해요!";
            case "relationship":
                return "당신은 관계지향형입니다. 함께하는 사람들과 소통하며 운동하는 것을 즐겨요!";
            case "recovery":
                return "당신은 회복지향형입니다. 몸과 마음의 휴식을 중요하게 생각해요!";
            case "learning":
                return "당신은 학습지향형입니다. 운동의 원리를 이해하고 배우는 것을 즐겨요!";
            case "habit":
                return "당신은 습관지향형입니다. 꾸준히 운동하는 습관을 들이는 것을 좋아해요!";
            default:
                return "균형 잡힌 성향입니다.";
        }
    }

    public boolean hasUserCompletedTendencyTest(Integer userId) {
        ChallengeFinalTestResult result = challengeMapper.findTestResultByUserId(userId);
        return result != null;
    }

    public ChallengeFinalTestResult findTestResult(int userId) {
        return challengeMapper.findTestResultByUserId(userId);
    }








    // 결제 준비 로직
    public PaymentReadyResponse startChallengeWithPayment(int userId, int challengeId) {
        // 챌린지 보증금과 제목 조회 (Mybatis XML에 있는 SQL ID 사용)
        int totalAmount = challengeMapper.findChallengeDepositAmount(challengeId);
        String challengeTitle = challengeMapper.findChallengeTitleById(challengeId);

        // PaymentService에 결제 준비 요청 위임
        return paymentService.kakaoPayReady(
                Long.valueOf(challengeId),
                userId,
                challengeTitle,
                totalAmount
        );
    }
    
    // 결제 승인 성공 후 최종 처리 (추첨권 지급 로직 제거)
    public void finalizeChallengeJoin(int userId, int challengeId) {
        challengeMapper.increaseChallengeParticipantCountInfo(challengeId);
        challengeMapper.insertUserChallengeInfo(userId, challengeId);
    }

}
