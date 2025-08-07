package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ChallengeDetailResponse {
    private int challengeId;
    private String challengeCreator;
    private String challengeTitle;
    private String challengeDescription;
    private int challengeMaxMembers;
    private int participantCount;

    private LocalDate challengeRecruitStartDate;
    private LocalDate challengeRecruitEndDate;

    private int challengeDurationDays;

    private String challengeThumbnailPath;
    
    // 키워드 처리를 위한 필드
    private List<String> challengeKeywords; // 서비스에서 파싱된 최종 키워드 리스트

    // !!! 중요: XML에서 String으로 가져온 키워드를 담을 임시 필드입니다.
    // Mybatis는 이 필드에 DB에서 가져온 콤마 구분 문자열을 직접 매핑합니다.
    // 서비스 레이어에서 이 필드 값을 List<String> challengeKeywords로 변환한 후,
    // 이 필드(challengeKeywordsString)는 null로 설정하여 클라이언트에는 보내지 않습니다.
    private String challengeKeywordsString; // SQL 쿼리의 'AS challengeKeywordsString'과 일치해야 합니다.


    private boolean userParticipating;

    private Integer challengeDepositAmount;
}