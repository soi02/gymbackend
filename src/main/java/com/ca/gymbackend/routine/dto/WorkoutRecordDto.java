package com.ca.gymbackend.routine.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkoutRecordDto {
    private int recordId;
    private int routineId;
    private LocalDate date;
    private int hours;
    private int minutes;
    private String memo;
    private String picture;
}
