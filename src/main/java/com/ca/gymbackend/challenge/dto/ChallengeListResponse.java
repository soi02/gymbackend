package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ChallengeListResponse {
    private int challengeId;
    private String challengeTitle;
    private String challengeThumbnailPath;
    private LocalDate challengeRecruitStartDate;
    private LocalDate challengeRecruitEndDate;
    private int challengeDurationDays;
    private int challengeMaxMembers;
    private int challengeParticipantCount;
    private List<String> keywords; // 프론트엔드에서 사용하는 키워드 이름 리스트
    private String status; // '모집 중', '모집 예정', '모집 종료' 등

    // MyBatis에서 사용할 임시 필드
    private String keywordNamesString;
}