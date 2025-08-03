package com.ca.gymbackend.challenge.service;

import com.ca.gymbackend.challenge.dto.ChallengeAttendanceRecord;
import com.ca.gymbackend.challenge.dto.ChallengeAttendanceStatus;
import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.dto.ChallengeDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeInfo;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.dto.ChallengeNorigaeInfo;
import com.ca.gymbackend.challenge.dto.ChallengeProgressResponse;
import com.ca.gymbackend.challenge.dto.ChallengeRecordInfo;
import com.ca.gymbackend.challenge.dto.ChallengeUserInfo;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl {

    private final ChallengeMapper challengeMapper;

    @Autowired
    @Qualifier("fileRootPath") // application.yml 또는 @Bean에서 설정한 경로 주입
    private String rootPath;

    // 챌린지 생성
    // 1. 이미지 저장
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


            Thumbnails.of(inputStream)
                      .scale(1.0)
                      .toFile(filePath.toFile());

            return "/challengeImages/" + todayPath + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // 2. 챌린지 정보 DB에 저장 insert
    public void saveChallengeData(ChallengeCreateRequest challengeCreateRequest) {
        challengeMapper.createChallenge(challengeCreateRequest);
    }

    // 3. 방금 생성된 챌린지 ID 가져오기 조회
    public int getGeneratedChallengeId() {
        return challengeMapper.findLastInsertedChallengeId();
    }

    // 4. 챌린지-키워드 연결 insert
    public void saveChallengekeywordMapping(int challengeId, List<String> selectedKeywordNameList) {
        if (selectedKeywordNameList != null && !selectedKeywordNameList.isEmpty()) { // null 체크와 비어있는지 체크
            for (String keywordName : selectedKeywordNameList) { // 키워드 이름을 순회
                Integer keywordId = challengeMapper.findKeywordIdByKeywordName(keywordName); // 키워드 이름으로 키워드 ID 조회

                if (keywordId != null) {
                    challengeMapper.createChallengeKeyword(challengeId, keywordId);
                } else {
                    System.err.println("경고: 키워드 '" + keywordName + "'에 해당하는 ID를 찾을 수 없습니다. 챌린지 " + challengeId + "에 연결되지 않았습니다.");
                }
            }
        }
    }
    

    // 챌린지 전체 목록 조회
    public List<ChallengeCreateRequest> getAllChallengeList() {
        return challengeMapper.findAllChallengeList();
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


    // **새로 추가된 메서드**: 일일 인증 사진을 로컬에 저장하고 URL을 반환합니다.
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



    // 일일 인증 기록을 저장하는 메서드

@Transactional
public void attendChallenge(int userId, int challengeId, MultipartFile photo) {
    LocalDate today = LocalDate.now();

    try {
        // 1. 오늘 날짜로 이미 인증했는지 확인
        int existingRecordCount = challengeMapper.countTodayAttendance(userId, challengeId, today);
        if (existingRecordCount > 0) {
            System.out.println("DEBUG: 이미 오늘 날짜로 인증했으므로 로직 중단.");
            throw new IllegalStateException("오늘 이미 인증했습니다.");
        }

        // 2. 사진 파일 업로드
        String photoUrl = saveAttendancePhoto(photo); 
        System.out.println("DEBUG: 사진 파일 업로드 성공. URL: " + photoUrl);

        // 3. DB에 출석 기록 저장
        challengeMapper.insertAttendanceRecord(userId, challengeId, today, photoUrl);
        System.out.println("DEBUG: 출석 기록 DB 저장 성공.");

        // 4. 노리개 지급 (시나리오 1)
        // 총 달성 일수와 챌린지 총 기간을 가져와 달성률 계산
        int totalAttendedDays = challengeMapper.countAttendedDays(challengeId, userId);
        int totalChallengeDays = challengeMapper.findChallengeTotalDays(challengeId);
        
        System.out.println("DEBUG: totalAttendedDays = " + totalAttendedDays);
        System.out.println("DEBUG: totalChallengeDays = " + totalChallengeDays);

        if (totalChallengeDays > 0) {
            System.out.println("DEBUG: totalChallengeDays > 0 조건 만족.");
            // 달성률 계산
            double achievementRate = (double) totalAttendedDays / totalChallengeDays * 100;
            // 달성률을 정수형으로 변환 (소수점 버림)
            int intAchievementRate = (int) achievementRate;
            System.out.println("DEBUG: achievementRate = " + achievementRate + ", intAchievementRate = " + intAchievementRate);

            // 달성률에 맞는 노리개 등급 조회
            Integer awardedTierId = challengeMapper.findTierIdByAchievementRate(intAchievementRate);
            System.out.println("DEBUG: awardedTierId = " + awardedTierId); 
            
            if (awardedTierId != null) {
                System.out.println("DEBUG: awardedTierId가 null이 아님. 노리개 지급 로직 계속 진행.");
                // 현재 사용자가 획득한 노리개 등급이 있는지 확인
                Integer existingTierId = challengeMapper.findUserNorigaeTierId(userId, challengeId);
                System.out.println("DEBUG: 기존 노리개 등급 ID (existingTierId) = " + existingTierId);
                
                if (existingTierId == null) {
                    // 획득한 등급이 없다면 새로 지급
                    challengeMapper.insertUserNorigae(userId, challengeId, awardedTierId);
                    System.out.println("INFO: 새로운 노리개 등급(" + awardedTierId + ")이 지급되었습니다! (INSERT)");
                } else if (awardedTierId > existingTierId) {
                    // 기존 등급보다 더 높은 등급을 달성했다면 업데이트
                    challengeMapper.updateUserNorigae(userId, challengeId, awardedTierId);
                    System.out.println("INFO: 노리개 등급이 " + existingTierId + "에서 " + awardedTierId + "로 업데이트되었습니다! (UPDATE)");
                } else {
                    System.out.println("INFO: 현재 등급보다 높은 등급이 아니므로 변경 없음.");
                }
            } else {
                System.out.println("DEBUG: awardedTierId가 null임. 노리개 지급 로직 중단.");
            }
        } else {
            System.out.println("DEBUG: totalChallengeDays가 0 이하임. 노리개 지급 로직 실행 안됨.");
        }

    } catch (IOException e) {
        System.err.println("ERROR: 사진 파일 업로드 실패: " + e.getMessage());
        throw new RuntimeException("사진 파일 업로드 실패", e);
    }
}


}
