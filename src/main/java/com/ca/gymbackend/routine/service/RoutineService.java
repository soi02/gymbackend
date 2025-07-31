package com.ca.gymbackend.routine.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.routine.dto.RoutineFromFrontDto;
import com.ca.gymbackend.routine.dto.RoutineInfoDto;
import com.ca.gymbackend.routine.dto.WorkoutPlanDto;
import com.ca.gymbackend.routine.dto.WorkoutSetDto;
import com.ca.gymbackend.routine.mapper.RoutineSqlMapper;
import com.ca.gymbackend.routine.request.RoutineSaveRequest;
import com.ca.gymbackend.routine.response.EveryWorkoutList;
import com.ca.gymbackend.routine.response.WorkoutGuideList;

@Service
public class RoutineService {

    @Autowired
    private RoutineSqlMapper routineSqlMapper;

    
    public List<EveryWorkoutList> getArticleList() {
        return routineSqlMapper.findAllWorkout();
    }

    public List<WorkoutGuideList> getWorkoutGuide(int id) {
        return routineSqlMapper.findWorkoutGuide(id);
    }

    public void saveRoutine(RoutineSaveRequest request) {
        RoutineInfoDto routine = new RoutineInfoDto();
        routine.setUserId(request.getUserId());
        routine.setRoutineName(request.getRoutineName());
        routine.setCreatedAt(LocalDateTime.now());

        routineSqlMapper.insertRoutineInfo(routine);

        int routineId = routine.getRoutineId();

        for (RoutineFromFrontDto workout : request.getWorkouts()) {
            WorkoutPlanDto plan = new WorkoutPlanDto();
            plan.setRoutineId(routineId);
            plan.setElementId(workout.getElementId());
            plan.setCreatedAt(LocalDateTime.now());
            routineSqlMapper.insertWorkoutPlan(plan);

            int planId = plan.getPlanId();
            
            for (WorkoutSetDto set : workout.getSets()) {
    
                routineSqlMapper.insertWorkoutSet(planId, set);
            }
        }
    }
}
