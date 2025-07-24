package com.ca.gymbackend.routine.dto;

import lombok.Data;

@Data
public class WorkoutGuideDto {
    private int guideId;
    private int elementId;
    private String instruction;
    private String video;
}
