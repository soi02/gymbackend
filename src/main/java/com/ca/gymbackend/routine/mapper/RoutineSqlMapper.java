package com.ca.gymbackend.routine.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.routine.dto.RoutineDto;
import com.ca.gymbackend.routine.dto.RoutineDetailDto;
import com.ca.gymbackend.routine.dto.RoutineSetDto;
import com.ca.gymbackend.routine.response.EveryWorkoutList;
import com.ca.gymbackend.routine.response.RoutineByUserId;
import com.ca.gymbackend.routine.response.RoutineDetailResponseDto;
import com.ca.gymbackend.routine.response.WorkoutGuideList;

@Mapper
public interface RoutineSqlMapper {
    public List<EveryWorkoutList> findAllWorkout();
    public List<WorkoutGuideList> findWorkoutGuide(int id);
    public void insertRoutineInfo(RoutineDto routine);
    public void insertRoutineDetail(RoutineDetailDto detail);
    public void insertRoutineSet(RoutineSetDto set);
    public List<RoutineByUserId> findRoutinesByUserId(int userId);
    List<RoutineDetailResponseDto> findRoutineDetailByRoutineId(@Param("routineId") int routineId);
    String findRoutineNameByRoutineId(@Param("routineId") int routineId);


}
