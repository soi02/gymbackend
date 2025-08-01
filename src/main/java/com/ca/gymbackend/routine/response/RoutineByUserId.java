package com.ca.gymbackend.routine.response;

import lombok.Data;

@Data
public class RoutineByUserId {
    private int routineId;
    private int userId;
    private String routineName;
    private int detailId;
    private int elementId;
    private int elementOrder;
    private int setId;
    private int kg;
    private int reps;

}
