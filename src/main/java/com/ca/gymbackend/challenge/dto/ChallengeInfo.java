package com.ca.gymbackend.challenge.dto;

import lombok.Data;

@Data
public class ChallengeInfo {
    private int challengeId;
    private String challengeTitle;
    private String challengeDescription;
    private String challengeThumbnailPath;
    private int challengeDurationDays;
    private int daysAttended; // 총 인증 횟수
}
