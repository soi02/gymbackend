package com.ca.gymbackend.routine.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WorkoutPlanDto {
    private int planId;
    private int routineId;
    private int elementId;
    private int elementOrder;
    private int setId;
    private LocalDateTime createdAt;
}


