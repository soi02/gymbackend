package com.ca.gymbackend.routine.dto;


import lombok.Data;

@Data
public class RoutineDetailDto {
    private int detailId;
    private int routineId;
    private int elementId;
    private int elementOrder;
}
