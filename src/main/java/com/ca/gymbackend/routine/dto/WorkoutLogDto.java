package com.ca.gymbackend.routine.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkoutLogDto {
    private int logId;
    private int userId;
    private int workoutId;
    private LocalDate date;
    private int hours;
    private int minutes;
    private int calories;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String memo;
    private String pictureUrl;
    private LocalDateTime createdAt;
}

