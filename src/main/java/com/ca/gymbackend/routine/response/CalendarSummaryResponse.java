package com.ca.gymbackend.routine.response;

import lombok.Data;

@Data
public class CalendarSummaryResponse {
    private int workoutCount;
    private int calories;
    private int workoutMinutes; // 분
}
