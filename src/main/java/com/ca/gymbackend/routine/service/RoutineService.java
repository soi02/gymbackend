package com.ca.gymbackend.routine.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.routine.dto.WorkoutElementDto;
import com.ca.gymbackend.routine.mapper.RoutineSqlMapper;

@Service
public class RoutineService {

    @Autowired
    private RoutineSqlMapper routineSqlMapper;

    
    public  List<WorkoutElementDto> getArticleList() {
        return routineSqlMapper.findAllWorkout();
    }
}
