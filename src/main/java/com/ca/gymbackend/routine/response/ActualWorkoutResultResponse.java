package com.ca.gymbackend.routine.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ActualWorkoutResultResponse {
    private LocalDateTime createdAt;
    private Integer detailId;
    private String elementName;
    private String elementPicture;
    private Integer setId;
    private Double kg;
    private Integer reps;
}
