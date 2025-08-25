package com.ca.gymbackend.challenge.dto;

import lombok.Data;

@Data
public class WeeklySummaryResponse {
    private int userId;
    private int totalDistinctDaysThisWeek; // 이번 주에 '하루라도' 인증한 날짜 수(중복 챌린지 제외)
    private int totalWeekDays = 7;         // 항상 7
    private int progressPercent;           // (totalDistinctDaysThisWeek / 7 * 100)
}