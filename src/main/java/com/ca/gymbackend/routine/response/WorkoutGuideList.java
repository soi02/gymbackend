package com.ca.gymbackend.routine.response;

import lombok.Data;

@Data
public class WorkoutGuideList {
    private int elementId;
    private String elementName;
    private String elementPicture;
    private String categoryName;
    private String instruction;     // 운동 방법
    private String memoContent;     // 운동 메모
}
