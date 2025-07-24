package com.ca.gymbackend.routine.dto;

import lombok.Data;

@Data
public class WorkoutRoutineDto {
    private int routineId;
    private int userId;
    private String routineName;
}
