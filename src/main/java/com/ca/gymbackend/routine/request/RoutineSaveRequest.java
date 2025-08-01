package com.ca.gymbackend.routine.request;

import java.util.List;

import com.ca.gymbackend.routine.dto.RoutineFromFrontDto;

import lombok.Data;

@Data
public class RoutineSaveRequest {
    private int userId;
    private String routineName;
    private List<RoutineFromFrontDto> workouts;



}
