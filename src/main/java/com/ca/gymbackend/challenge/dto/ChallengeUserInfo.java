package com.ca.gymbackend.challenge.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChallengeUserInfo {
    private String challengeTitle; // 추가
    private int totalPeriod; // 추가
    private LocalDateTime personalJoinDate;
    private LocalDateTime personalEndDate;
}
