package com.ca.gymbackend.challenge.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChallengeProgressResponse {
    private String challengeTitle;
    private int totalPeriod;
    // private int norigaeConditionRate;
    // private String norigaeName;
    // private String norigaeIconPath;
    private int myAchievement;

    private List<ChallengeAttendanceStatus> challengeAttendanceStatus;

    // 노리개 등급 관련 필드 추가
    private String awardedNorigaeName;  // 획득한 노리개 등급 이름 (예: Silver)
    private String awardedNorigaeIconPath; // 획득한 노리개 등급 이미지 경로
}
