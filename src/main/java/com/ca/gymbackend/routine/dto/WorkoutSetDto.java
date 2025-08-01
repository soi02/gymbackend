package com.ca.gymbackend.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkoutSetDto {
    private Integer setId;
    private double kg;
    private int reps;
    private int planId;
}


