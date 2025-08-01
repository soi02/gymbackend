package com.ca.gymbackend.challenge.service;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.dto.ChallengeDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    public ChallengeDetailResponse getChallengeDetailByChallengeId(int challengeId) {

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

        return challengeDetailResponse;
    }




    // 챌린지 도전 시작
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
}
