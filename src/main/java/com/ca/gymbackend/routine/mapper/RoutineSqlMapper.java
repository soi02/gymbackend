package com.ca.gymbackend.routine.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ca.gymbackend.routine.dto.RoutineInfoDto;
import com.ca.gymbackend.routine.dto.WorkoutPlanDto;
import com.ca.gymbackend.routine.dto.WorkoutSetDto;
import com.ca.gymbackend.routine.response.EveryWorkoutList;
import com.ca.gymbackend.routine.response.WorkoutGuideList;

@Mapper
public interface RoutineSqlMapper {
    public List<EveryWorkoutList> findAllWorkout();
    public List<WorkoutGuideList> findWorkoutGuide(int id);
    public void insertRoutineInfo(RoutineInfoDto routine);
    public void insertWorkoutPlan(WorkoutPlanDto plan);
    public void insertWorkoutSet(WorkoutSetDto set);
}
