package com.ca.gymbackend.routine.dto;

import lombok.Data;

@Data
public class RoutineSetDto {
    private int setId;
    private int detailId;
    private double kg;
    private int reps;
}
