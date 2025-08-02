package com.ca.gymbackend.routine.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoutineDetailResponse {
    private String routineName;
    private List<RoutineDetailResponseDto> details;
}
