package com.ca.gymbackend.challenge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.dto.ChallengeDetailResponse;
import com.ca.gymbackend.challenge.dto.ChallengeInfo;
import com.ca.gymbackend.challenge.dto.ChallengeMyRecordsResponse;
import com.ca.gymbackend.challenge.dto.ChallengeRecordInfo;

@Mapper
public interface ChallengeMapper {

    // 챌린지 생성

    // 생성 순서 1. 챌린지 정보를 DB 에 저장
    public void createChallenge(ChallengeCreateRequest challengeCreateRequest);

    // 생성 순서 2. 방금 DB 에 insert 된 챌린지의 프라이머리키 값(AutoIncrement)을 가져오기
    public int findLastInsertedChallengeId(); // AutoIncrement된 challenge_id 조회

    // 키워드 이름으로 키워드 ID 조회
    public Integer findKeywordIdByKeywordName(@Param("keywordName") String keywordName);

    // 생성 순서 3. 챌린지와 키워드 연결 (다대다 관계 처리)
    public void createChallengeKeyword(@Param("challengeId") int challengeId, @Param("keywordId") int keywordId);




    // 챌린지 가져오기 (목록)
    public List<ChallengeCreateRequest> findAllChallengeList();



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



}
