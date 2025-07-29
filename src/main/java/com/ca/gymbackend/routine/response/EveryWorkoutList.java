package com.ca.gymbackend.routine.response;
import lombok.Data;

@Data
public class EveryWorkoutList {
    private String categoryName;
    private int categoryId;
    private int elementId;
    private String elementName;
    private String elementPicture;
}
