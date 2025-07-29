package com.ca.gymbackend.routine.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ca.gymbackend.routine.dto.WorkoutGuideDto;
import com.ca.gymbackend.routine.response.EveryWorkoutList;

@Mapper
public interface RoutineSqlMapper {
    public List<EveryWorkoutList> findAllWorkout();
    public List<WorkoutGuideDto> findWorkoutGuide(int id);
}
