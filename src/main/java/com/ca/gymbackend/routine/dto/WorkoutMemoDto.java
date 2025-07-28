package com.ca.gymbackend.routine.dto;

import lombok.Data;

@Data
public class WorkoutMemoDto {
    private int memoId;
    private int elementId;
    private String memoContent;
}

