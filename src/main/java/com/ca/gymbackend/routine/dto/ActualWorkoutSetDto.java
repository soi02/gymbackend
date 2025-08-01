package com.ca.gymbackend.routine.dto;

import lombok.Data;

@Data
public class ActualWorkoutSetDto {
    private int setId;
    private int detailId;
    private double kg;
    private int reps;
}
