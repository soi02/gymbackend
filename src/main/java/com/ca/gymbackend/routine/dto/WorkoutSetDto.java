package com.ca.gymbackend.routine.dto;

import lombok.Data;

@Data
public class WorkoutSetDto {
    private int setId;
    private int elementId;
    private double kg;
    private int reps;
}

