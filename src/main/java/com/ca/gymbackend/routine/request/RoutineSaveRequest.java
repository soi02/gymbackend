package com.ca.gymbackend.routine.request;

import java.util.List;


import lombok.Data;

@Data
public class RoutineSaveRequest {
    private int userId;
    private String routineName;
    private List<RoutineSaveDetailDto> routineDetailList;

}
