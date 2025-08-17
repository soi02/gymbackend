package com.ca.gymbackend.routine.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WorkoutCardResponse {
    private int workoutId;
    private int setCount; 
    private int workoutCount;
    private int calories;
    private double totalVolume;
    private String memo;
    private String pictureUrl; 
    private LocalDateTime createdAt;
}
