package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;
// import java.util.List;


import lombok.Data;

@Data
public class ChallengeCreateRequest {
    private int challengeId;
    private String challengeCreator;
    private String challengeTitle;
    private String challengeDescription;
    private int challengeMaxMembers;
    private LocalDate challengeRecruitStartDate; // 모집 기간
    private LocalDate challengeRecruitEndDate; // 모집 기간
    private int challengeDurationDays; // 진행 기간
    // private List<Integer> challengeKeywordIds;


    private String challengeThumbnailPath; // 파일 저장 경로
    
}
