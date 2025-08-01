package com.ca.gymbackend.routine.dto;

import lombok.Data;

@Data
public class ActualWorkoutDetailDto {
    private int detailId;
    private int workoutId;
    private int elementId;
    private int elementOrder;
}
