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
    private int participantCount ; // 현재 참여자 수

    private LocalDate challengeStartDate;
    private LocalDate challengeEndDate;
    private int challengeDurationDays;

    private String challengeThumbnailPath;
    
    private String challengeStatus; // "진행 중", "종료", "예정" 등

    private List<String> challengeKeywords; // ID 대신 키워드 이름으로 변경될 수 있음

    // !!! 중요: XML에서 String으로 가져온 키워드를 담을 임시 필드입니다.
    // Mybatis는 이 필드에 DB에서 가져온 콤마 구분 문자열을 직접 매핑합니다.
    private String challengeKeywordsString; // SQL 쿼리의 별칭과 일치해야 합니다.
}
