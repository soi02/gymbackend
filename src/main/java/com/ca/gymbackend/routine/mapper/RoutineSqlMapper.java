package com.ca.gymbackend.routine.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ca.gymbackend.routine.response.EveryWorkoutList;
import com.ca.gymbackend.routine.response.WorkoutGuideList;

@Mapper
public interface RoutineSqlMapper {
    public List<EveryWorkoutList> findAllWorkout();
    public List<WorkoutGuideList> findWorkoutGuide(int id);
}
