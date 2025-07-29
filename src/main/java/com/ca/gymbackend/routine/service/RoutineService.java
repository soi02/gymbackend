package com.ca.gymbackend.routine.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.routine.dto.WorkoutGuideDto;
import com.ca.gymbackend.routine.mapper.RoutineSqlMapper;
import com.ca.gymbackend.routine.response.EveryWorkoutList;

@Service
public class RoutineService {

    @Autowired
    private RoutineSqlMapper routineSqlMapper;

    
    public  List<EveryWorkoutList> getArticleList() {
        return routineSqlMapper.findAllWorkout();
    }

    public List<WorkoutGuideDto> getWorkoutGuide(int id) {
        return routineSqlMapper.findWorkoutGuide(id);
    }
}
