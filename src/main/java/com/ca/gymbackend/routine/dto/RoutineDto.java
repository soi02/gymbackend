package com.ca.gymbackend.routine.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RoutineDto {
    private int routineId;
    private int userId;
    private String routineName;
    private LocalDateTime createdAt;
}
