package com.ca.gymbackend.challenge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;

@Mapper
public interface ChallengeMapper {

    // 챌린지 생성

    // 생성 순서 1. 챌린지 정보를 DB 에 저장
    public void createChallenge(ChallengeCreateRequest challengeCreateRequest);

    // 생성 순서 2. 방금 DB 에 insert 된 챌린지의 프라이머리키 값(AutoIncrement)을 가져오기
    public int findLastInsertedChallengeId(); // AutoIncrement된 challenge_id 조회

    // 생성 순서 3. 챌린지와 키워드 연결 (다대다 관계 처리)
    public void createChallengeKeyword(@Param("challengeId") int challengeId, @Param("challengeKeywordId") int challengeKeywordId);




    // 챌린지 가져오기 (목록)
    public List<ChallengeCreateRequest> findAllChallengeList();
    


}
