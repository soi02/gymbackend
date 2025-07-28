package com.ca.gymbackend.routine.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ca.gymbackend.routine.dto.WorkoutElementDto;

@Mapper
public interface RoutineSqlMapper {
    public List<WorkoutElementDto> findAllWorkout();
}
