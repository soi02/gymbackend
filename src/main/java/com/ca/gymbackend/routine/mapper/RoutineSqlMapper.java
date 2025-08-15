package com.ca.gymbackend.routine.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.routine.dto.RoutineDto;
import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.routine.dto.ActualWorkoutDetailDto;
import com.ca.gymbackend.routine.dto.ActualWorkoutDto;
import com.ca.gymbackend.routine.dto.ActualWorkoutSetDto;
import com.ca.gymbackend.routine.dto.RoutineDetailDto;
import com.ca.gymbackend.routine.dto.RoutineSetDto;
import com.ca.gymbackend.routine.dto.WorkoutLogDto;
import com.ca.gymbackend.routine.response.ActualWorkoutResultResponse;
import com.ca.gymbackend.routine.response.CalendarSummaryResponse;
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
    public List<RoutineDetailResponseDto> findRoutineDetailByRoutineId(@Param("routineId") int routineId);
    public List<RoutineSetDto> findRoutineSetsByRoutineId(@Param("routineId") int routineId);
    public String findRoutineNameByRoutineId(@Param("routineId") int routineId);
    public void insertActualWorkout(ActualWorkoutDto workout);
    public void insertActualWorkoutDetail(ActualWorkoutDetailDto detail);
    public void insertActualWorkoutSet(ActualWorkoutSetDto set);
    public void insertWorkoutLog(WorkoutLogDto log);
    public UserDto findUserById(@Param("id") int id);
    public List<ActualWorkoutResultResponse> findWorkoutResultByWorkoutId(int workoutId);
    public List<CalendarSummaryResponse> findWorkoutResultByDate(@Param("userId") int userId, @Param("date") String date);
    public List<String> findWorkoutDatesBetween(@Param("userId") int userId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    public void deleteMemoByElementId(@Param("elementId") int elementId);
    public void insertMemo(@Param("elementId") int elementId, @Param("memoContent") String memoContent);
    public WorkoutLogDto findWorkoutLogByWorkoutId(int workoutId);

    public void updateWorkoutLogExtras(@Param("workoutId") int workoutId,
                                @Param("memo") String memo,
                                @Param("pictureUrl") String pictureUrl);

    public void insertEmptyWorkoutLog(@Param("workoutId") int workoutId);
}
