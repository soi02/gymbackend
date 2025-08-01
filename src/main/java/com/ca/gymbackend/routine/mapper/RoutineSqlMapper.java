package com.ca.gymbackend.routine.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ca.gymbackend.routine.dto.Routine;
import com.ca.gymbackend.routine.dto.RoutineSet;
import com.ca.gymbackend.routine.response.EveryWorkoutList;
import com.ca.gymbackend.routine.response.WorkoutGuideList;

@Mapper
public interface RoutineSqlMapper {
    public List<EveryWorkoutList> findAllWorkout();
    public List<WorkoutGuideList> findWorkoutGuide(int id);
    public void insertRoutineInfo(Routine routine);
    // public void insertWorkoutPlan(WorkoutPlanDto plan);
    // public void insertWorkoutSet(RoutineSet set);
}
