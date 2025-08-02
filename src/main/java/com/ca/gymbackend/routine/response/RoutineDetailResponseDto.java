package com.ca.gymbackend.routine.response;

import lombok.Data;

@Data
public class RoutineDetailResponseDto {
    private int detailId;
    private int elementId;
    private String elementName;
    private String categoryName;
    private String elementPicture; 
}
