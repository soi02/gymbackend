package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ChallengeDetailResponse {
    private int challengeId;
    private String challengeCreator;
    private String creatorProfileImagePath;
    private String challengeTitle;
    private String challengeDescription;
    private int challengeDepositAmount;
    private int challengeMaxMembers;
    private int participantCount; // 참여자 수는 participantCount로 명명
    private LocalDate challengeRecruitStartDate;
    private LocalDate challengeRecruitEndDate;
    private int challengeDurationDays;
    private String challengeThumbnailPath;
    private List<String> keywords; // 상세 페이지용 키워드 이름 리스트
    private String status;
    private boolean userParticipating;

    // MyBatis에서 사용할 임시 필드
    private String keywordNamesString;
}