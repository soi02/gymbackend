package com.ca.gymbackend.routine.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActualWorkoutSaveRequest {
    private int userId;
    private int routineId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ActualWorkoutDetailDto> details;

    @Data
    public static class ActualWorkoutDetailDto {
        private int elementId;
        private int order;
        private List<ActualWorkoutSetDto> sets;

        @Data
        public static class ActualWorkoutSetDto {
            private double kg;
            private int reps;
        }
    }
}


