package com.ca.gymbackend.routine.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ActualWorkoutDto {
    private int workoutId;
    private int userId;
    private int routineId;
    private LocalDateTime createdAt;
}
