package com.ca.gymbackend.routine.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.routine.dto.RoutineSetDto;
import com.ca.gymbackend.routine.dto.WorkoutLogDto;
import com.ca.gymbackend.routine.dto.RoutineDto;
import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.routine.dto.ActualWorkoutDetailDto;
import com.ca.gymbackend.routine.dto.ActualWorkoutDto;
import com.ca.gymbackend.routine.dto.ActualWorkoutSetDto;
import com.ca.gymbackend.routine.dto.RoutineDetailDto;
// import com.ca.gymbackend.routine.dto.WorkoutPlanDto;
import com.ca.gymbackend.routine.mapper.RoutineSqlMapper;
import com.ca.gymbackend.routine.request.ActualWorkoutSaveRequest;
import com.ca.gymbackend.routine.request.RoutineSaveDetailDto;
import com.ca.gymbackend.routine.request.RoutineSaveRequest;
import com.ca.gymbackend.routine.request.RoutineSaveSetDto;
import com.ca.gymbackend.routine.response.ActualWorkoutResultResponse;
import com.ca.gymbackend.routine.response.CalendarSummaryResponse;
import com.ca.gymbackend.routine.response.EveryWorkoutList;
import com.ca.gymbackend.routine.response.RoutineByUserId;
import com.ca.gymbackend.routine.response.RoutineDetailResponse;
import com.ca.gymbackend.routine.response.RoutineDetailResponseDto;
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
        // 1️⃣ 루틴 저장
        // 루틴 테이블에 새로운 루틴을 등록.
        // 참고

        // const payload = {
        //     userId: Number(userId),
        //     routineName: routineName,
        //     routineDetailList: routineData.map((workout, idx) => ({
        //         elementId: workout.elementId,
        //         elementOrder: idx + 1,
        //         setList: workout.sets.map(set => ({
        //             kg: Number(set.weight || 0),
        //             reps: Number(set.reps || 0)
        //         }))
        //     }))
        // };



        RoutineDto routine = new RoutineDto();
        // 프론트에서 받아온 RoutineSaveRequest 데이터에서 userid, routinename을 꺼내와서 세팅
        routine.setUserId(request.getUserId());
        routine.setRoutineName(request.getRoutineName());
        routine.setCreatedAt(LocalDateTime.now());
        routineSqlMapper.insertRoutineInfo(routine);

        // 위에서 저장한 루틴의 PK를 가져옴.
        int routineId = routine.getRoutineId();

        // 2️⃣ 루틴 상세 저장
        // 프론트에서 온 RoutineDetailList를 반복함
        for (RoutineSaveDetailDto detailDto : request.getRoutineDetailList()) {
            // 운동 상세 insert용 객체
            RoutineDetailDto detail = new RoutineDetailDto();
            detail.setRoutineId(routineId);
            detail.setElementId(detailDto.getElementId());
            detail.setElementOrder(detailDto.getElementOrder());
            routineSqlMapper.insertRoutineDetail(detail);

            int detailId = detail.getDetailId();

            // 3️⃣ 세트 저장
            for (RoutineSaveSetDto setDto : detailDto.getSetList()) {
                RoutineSetDto set = new RoutineSetDto();
                set.setDetailId(detailId);
                set.setKg(setDto.getKg());
                set.setReps(setDto.getReps());
                routineSqlMapper.insertRoutineSet(set);
            }
        }
    }

    public List<RoutineByUserId> getRoutinesByUserId(int userId) {
        return routineSqlMapper.findRoutinesByUserId(userId);
    }

    public RoutineDetailResponse getRoutineDetail(int routineId) {
        String routineName = routineSqlMapper.findRoutineNameByRoutineId(routineId);
        List<RoutineDetailResponseDto> detailList = routineSqlMapper.findRoutineDetailByRoutineId(routineId);
        List<RoutineSetDto> sets = routineSqlMapper.findRoutineSetsByRoutineId(routineId);

        return new RoutineDetailResponse(routineName, detailList, sets);
    }


    public int saveActualWorkout(ActualWorkoutSaveRequest request) {
        // 1. actual_workout 저장
        ActualWorkoutDto workout = new ActualWorkoutDto();
        workout.setUserId(request.getUserId());
        workout.setRoutineId(request.getRoutineId());
        workout.setCreatedAt(LocalDateTime.now());

        routineSqlMapper.insertActualWorkout(workout); // workout_id 생성됨
        int workoutId = workout.getWorkoutId();

        // 2. detail + set 저장
        for (ActualWorkoutSaveRequest.ActualWorkoutDetailDto detailDto : request.getDetails()) {
            // actual_workout_detail 저장
            ActualWorkoutDetailDto detail = new ActualWorkoutDetailDto();
            detail.setWorkoutId(workoutId);
            detail.setElementId(detailDto.getElementId());
            detail.setElementOrder(detailDto.getOrder());

            routineSqlMapper.insertActualWorkoutDetail(detail);
            int detailId = detail.getDetailId();

            // actual_workout_set 저장
            for (ActualWorkoutSaveRequest.ActualWorkoutDetailDto.ActualWorkoutSetDto setDto : detailDto.getSets()) {
                ActualWorkoutSetDto set = new ActualWorkoutSetDto();
                set.setDetailId(detailId);
                set.setKg(setDto.getKg());
                set.setReps(setDto.getReps());

                routineSqlMapper.insertActualWorkoutSet(set);
            }
        }
        // 3. workout_log 저장
        WorkoutLogDto log = new WorkoutLogDto();
        log.setUserId(request.getUserId());
        log.setWorkoutId(workoutId);
        log.setStartTime(request.getStartTime());
        log.setEndTime(request.getEndTime());

        // 날짜만 따로 추출해서 세팅 (DATE 타입 컬럼)
        log.setDate(request.getStartTime().toLocalDate());

        // 시간 계산
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        log.setMinutes((int) minutes);
        log.setHours((int) (minutes / 60.0));

        // createdAt
        log.setCreatedAt(LocalDateTime.now());

        // 칼로리계산
        UserDto user = routineSqlMapper.findUserById(request.getUserId());

        double weight = user.getWeight();       // 체중 (kg)
        double height = user.getHeight();       // 키 (cm)
        double muscleMass = user.getMuscleMass(); // 골격근량 (kg)
        String gender = user.getGender();       // "M" or "F"
        double hours = (double) minutes / 60.0;

        // 1️⃣ BMR 계산 (나이 없이)
        double bmr = gender.equals("M")
            ? 10 * weight + 6.25 * height + 5
            : 10 * weight + 6.25 * height - 161;

        // 2️⃣ METs 보정 (골격근량 비율로 조정)
        double muscleRatio = muscleMass / weight;
        double mets = 3.5 + muscleRatio * 1.5; // 기준 METs: 3.5 + 근육비율 보정

        // 3️⃣ 총 칼로리 계산
        int calories = (int)((bmr / 24.0) * mets * hours);

        log.setCalories(calories);


        routineSqlMapper.insertWorkoutLog(log);

        return workoutId;

    }

    public List<ActualWorkoutResultResponse> getWorkoutResult(int workoutId) {
        return routineSqlMapper.findWorkoutResultByWorkoutId(workoutId);
    }

    public List<CalendarSummaryResponse> getWorkoutByDate(int userId, String date) {
        return routineSqlMapper.findWorkoutResultByDate(userId, date);
    }

    public List<String> getWorkoutDatesBetween(int userId, String startDate, String endDate) {
        return routineSqlMapper.findWorkoutDatesBetween(userId, startDate, endDate);
    }

}
