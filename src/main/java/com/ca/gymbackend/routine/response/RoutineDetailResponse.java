package com.ca.gymbackend.routine.response;

import java.util.List;

import com.ca.gymbackend.routine.dto.RoutineSetDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoutineDetailResponse {
    private String routineName;
    private List<RoutineDetailResponseDto> details;
    private List<RoutineSetDto> sets;
}
