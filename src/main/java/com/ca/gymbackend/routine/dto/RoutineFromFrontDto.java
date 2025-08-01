package com.ca.gymbackend.routine.dto;

import java.util.List;

import lombok.Data;

@Data
public class RoutineFromFrontDto {
    private int elementId;
    private List<WorkoutSetDto> sets;
}
