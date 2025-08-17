package com.ca.gymbackend.routine.response;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class WorkoutCardResponse {
    private int workoutId;
    private int setCount;        // 세트 수
    private int workoutCount;    // 운동(종목) 수 = distinct element_id
    private int calories;        // 칼로리 (workout_log 기준)
    private double totalVolume;  // Σ(kg * reps)
        private String memo;        // ← 추가
    private String pictureUrl;  // ← 추가
    private LocalDateTime createdAt;
}
