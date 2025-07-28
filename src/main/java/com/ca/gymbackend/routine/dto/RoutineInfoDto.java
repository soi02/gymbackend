package com.ca.gymbackend.routine.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RoutineInfoDto {
    private int routineId;
    private int userId;
    private String routineName;
    private LocalDateTime createdAt;
}
