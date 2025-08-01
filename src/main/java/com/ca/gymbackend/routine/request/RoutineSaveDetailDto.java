package com.ca.gymbackend.routine.request;

import java.util.List;

import lombok.Data;

@Data
public class RoutineSaveDetailDto {
    private int elementId;
    private int elementOrder;
    private List<RoutineSaveSetDto> setList;
}
