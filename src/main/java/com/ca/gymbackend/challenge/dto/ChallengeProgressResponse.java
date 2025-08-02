package com.ca.gymbackend.challenge.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChallengeProgressResponse {
    private String challengeTitle;
    private int totalPeriod;
    private int norigaeConditionRate;
    private String norigaeName;
    private String norigaeIconPath;
    private int myAchievement;
    
    private List<ChallengeAttendanceStatus> challengeAttendanceStatus;
}
