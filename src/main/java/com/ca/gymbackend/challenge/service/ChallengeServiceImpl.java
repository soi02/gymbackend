package com.ca.gymbackend.challenge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl {
    
    private final ChallengeMapper challengeMapper;

    // 챌린지 생성
    public void registerChallenge(ChallengeCreateRequest challengeCreateRequest) {
        challengeMapper.createChallenge(challengeCreateRequest); // 생성 순서 1. 챌린지 insert (챌린지 DB에 저장)

        // 2. 해당 challenge_id 가져오기 (생성된 ID 기준으로 다시 select)
        int challengeId = challengeMapper.findLastInsertedChallengeId();

        // 3. 챌린지 - 키워드 연결 (중간다리 테이블에 insert)
        for(Integer challengeKeywordId : challengeCreateRequest.getChallengeKeywordIds()) {
            challengeMapper.createChallengeKeyword(challengeId, challengeKeywordId);
        }
    }


    // 챌린지 가져오기 (목록)
    public List<ChallengeCreateRequest> getAllChallengeList(){
        return challengeMapper.findAllChallengeList();
    }
    
}
