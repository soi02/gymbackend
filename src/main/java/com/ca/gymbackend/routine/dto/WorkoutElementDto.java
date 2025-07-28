package com.ca.gymbackend.routine.dto;

import lombok.Data;

@Data
public class WorkoutElementDto {
    private int elementId;
    private int categoryId;
    private String elementName;
    private String elementPicture;
}

