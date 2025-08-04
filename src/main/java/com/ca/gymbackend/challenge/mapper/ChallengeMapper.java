package com.ca.gymbackend.challenge.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.challenge.dto.ChallengeAttendanceRecord;
import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.dto.ChallengeDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeFinalTestResult;
import com.ca.gymbackend.challenge.dto.ChallengeInfo;
import com.ca.gymbackend.challenge.dto.ChallengeKeywordCategory;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.dto.ChallengeProgressResponse;
import com.ca.gymbackend.challenge.dto.ChallengeRecordInfo;
import com.ca.gymbackend.challenge.dto.ChallengeTestScore;
import com.ca.gymbackend.challenge.dto.ChallengeUserInfo;

@Mapper
public interface ChallengeMapper {

    // 챌린지 생성

    // 생성 순서 1. 챌린지 정보를 DB 에 저장
    public void createChallenge(ChallengeCreateRequest challengeCreateRequest);

    // 생성 순서 2. 챌린지와 키워드 연결 (다대다 관계 처리)
    public void createChallengeKeyword(@Param("challengeId") int challengeId, @Param("keywordId") int keywordId);




    // 챌린지 가져오기 (목록)
    public List<ChallengeCreateRequest> findAllChallengeList();

    // 챌린지 ID를 기준으로 키워드 ID 목록 조회
    public List<Integer> findKeywordIdsByChallengeId(int challengeId);



    public List<ChallengeKeywordCategory> findAllKeywordCategories();


    // 카테고리별 챌린지 목록 조회
    public List<ChallengeCreateRequest> findChallengesByCategoryId(@Param("categoryId") Integer categoryId);



    // 챌린지 상세보기
    public ChallengeDetailResponse findChallengeDetailByChallengeId(@Param("challengeId") int challengeId);
    


    // 챌린지 도전 시작
    // user_challenge 테이블에 이미 해당사용자와 챌린지Id 의 조합이 존재하는지 확인
    public int existsUserChallenge(@Param("userId") int userId, @Param("challengeId") int challengeId);

    // 1. user_challenge 테이블에 사용자 챌린지 정보를 삽입
    public void insertUserChallenge(@Param("userId") int userId, @Param("challengeId") int challengeId);

    // 2. challenge 테이블의 participant_count를 1 증가시키기
    public void increaseChallengeParticipantCount(@Param("challengeId") int challengeId);



    

    // 나의 수련기록
    // 내가 참여한 챌린지 목록 조회
    public List<ChallengeMyRecordsResponse> findAllMyChallengeList(@Param("userId") int userId);

    // 특정 챌린지의 총 출석 일수를 조회
    public int countAttendanceDays(@Param("userId") int userId, @Param("challengeId") int challengeId);

    // 특정 챌린지에서 오늘 출석했는지 출석여부 확인
    public int hasAttendedToday(@Param("userId") int userId, @Param("challengeId") int challengeId);






    // 특정 사용자의 특정 챌린지 상세 정보 & 인증 기록 조회
    // 챌린지 ID로 챌린지 상세 정보(ChallengeInfo) 조회
    public ChallengeInfo findChallengeInfoByChallengeId(@Param("challengeId") int challengeId);

    // 사용자 ID와 챌린지 ID로 인증 기록 리스트 조회
    public List<ChallengeRecordInfo> findChallengeRecordList(@Param("userId") int userId, @Param("challengeId") int challengeId);

    // 사용자 ID와 챌린지 ID로 인증 횟수(카운트) 조회
    public int countAttendanceRecordList(@Param("userId") int userId, @Param("challengeId") int challengeId);





    // 노리개
    // 사용자의 특정 챌린지 참여 정보(시작/종료일) 조회
    public ChallengeUserInfo findUserChallengeInfoByUserIdAndChallengeId(@Param("userId") int userId, @Param("challengeId") int challengeId);

    // 챌린지 상세 진행 상황 조회 (노리개 등급 정보 포함)
    ChallengeProgressResponse findChallengeProgressInfo(@Param("challengeId") int challengeId, @Param("userId") int userId);





    // 사용자의 출석 기록 조회
    public List<ChallengeAttendanceRecord> findAttendanceRecords(@Param("challengeId") int challengeId, @Param("userId") int userId);

    // 총 출석 일수 계산
    public int countAttendedDays(@Param("challengeId") int challengeId, @Param("userId") int userId);
    
    // 오늘 날짜로 이미 인증 기록이 있는지 확인
    public int countTodayAttendance(@Param("userId") int userId, @Param("challengeId") int challengeId, @Param("today") LocalDate today);

    // 출석 기록 삽입
    public void insertAttendanceRecord(@Param("userId") int userId, @Param("challengeId") int challengeId, 
                                @Param("attendanceDate") LocalDate attendanceDate, @Param("attendanceImagePath") String attendanceImagePath);




    // 노리개 지급 로직 관련 추가 메서드
    // 챌린지의 총 기간 조회
    public int findChallengeTotalDays(@Param("challengeId") int challengeId);
    
    // 달성률에 맞는 노리개 등급 ID 조회 (가장 높은 등급부터 조회)
    public Integer findTierIdByAchievementRate(@Param("rate") double rate);

    // 사용자가 현재 챌린지에서 획득한 노리개 등급 ID 조회
    public Integer findUserNorigaeTierId(@Param("userId") int userId, @Param("challengeId") int challengeId);

    // 사용자에게 노리개 등급 지급 (INSERT)
    public void insertUserNorigae(@Param("userId") int userId, @Param("challengeId") int challengeId, @Param("tierId") int tierId);

    // 사용자의 노리개 등급 업데이트 (UPDATE)
    public void updateUserNorigae(@Param("userId") int userId, @Param("challengeId") int challengeId, @Param("tierId") int tierId);





    // 키워드에 따른 챌린지 추천
    public List<ChallengeCreateRequest> findRecommendedChallengeList(@Param("keywordIds") List<Integer> keywordIds);






    // 성향 테스트 결과 저장
    // 사용자의 최종 성향 테스트 결과 저장
    public void insertTestScore(ChallengeTestScore challengeTestScore);

    public void insertFinalTestResult(ChallengeFinalTestResult challengeFinalTestResult);


    // 특정 사용자의 가장 최근 성향 테스트 결과 조회
    public ChallengeFinalTestResult findTestResultByUserId(@Param("userId") int userId);
}






