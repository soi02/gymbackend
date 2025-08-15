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
        // 1) workout 저장 (동일)
        ActualWorkoutDto workout = new ActualWorkoutDto();
        workout.setUserId(request.getUserId());
        workout.setRoutineId(request.getRoutineId());
        workout.setCreatedAt(LocalDateTime.now());
        routineSqlMapper.insertActualWorkout(workout);
        int workoutId = workout.getWorkoutId();

        // 🔢 누적용 변수
        double totalVolumeKg = 0.0; // Σ(kg*reps)
        int totalReps = 0;
        int totalSets = 0;

        // 2) detail + set 저장 (동일) 하면서 누적
        for (ActualWorkoutSaveRequest.ActualWorkoutDetailDto detailDto : request.getDetails()) {
            ActualWorkoutDetailDto detail = new ActualWorkoutDetailDto();
            detail.setWorkoutId(workoutId);
            detail.setElementId(detailDto.getElementId());
            detail.setElementOrder(detailDto.getOrder());
            routineSqlMapper.insertActualWorkoutDetail(detail);
            int detailId = detail.getDetailId();

            for (ActualWorkoutSaveRequest.ActualWorkoutDetailDto.ActualWorkoutSetDto setDto : detailDto.getSets()) {
                ActualWorkoutSetDto set = new ActualWorkoutSetDto();
                set.setDetailId(detailId);
                set.setKg(setDto.getKg());
                set.setReps(setDto.getReps());
                routineSqlMapper.insertActualWorkoutSet(set);

                double kg = setDto.getKg();   // 기본형 double
                int reps  = setDto.getReps(); // 기본형 int

                totalVolumeKg += kg * reps;
                totalReps     += reps;
                totalSets     += 1;

            }
        }

        // 3) workout_log 저장 (시간/칼로리 계산 개선)
        WorkoutLogDto log = new WorkoutLogDto();
        log.setUserId(request.getUserId());
        log.setWorkoutId(workoutId);
        log.setStartTime(request.getStartTime());
        log.setEndTime(request.getEndTime());
        log.setDate(request.getStartTime().toLocalDate());

        long minutesMeasured = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        log.setMinutes((int) minutesMeasured);
        log.setHours((int) (minutesMeasured / 60.0));
        log.setCreatedAt(LocalDateTime.now());

        // 🔥 칼로리 계산 - 하이브리드
        UserDto user = routineSqlMapper.findUserById(request.getUserId());
        double weight = user.getWeight();     // kg
        double height = user.getHeight();     // cm
        double muscleMass = user.getMuscleMass(); // kg (안 써도 됨)
        String gender = user.getGender();     // "M" or "F"

        // BMR (나이 없이)
        double bmr = "M".equals(gender)
            ? 10 * weight + 6.25 * height + 5
            : 10 * weight + 6.25 * height - 161;

        // ① 시간기반 (측정시간 vs 추정시간 중 큰 값 사용)
        final double MET_RT = 5.5;     // 저항운동 중고강도
        final double TUT_SEC = 3.0;    // rep 당 시간(초)
        final double REST_SEC = 60.0;  // 세트 간 평균 휴식(초) - 필요시 사용자 설정 반영 가능

        double estimatedMinutes = (totalReps * TUT_SEC + totalSets * REST_SEC) / 60.0;
        double effectiveHours = Math.max(minutesMeasured / 60.0, estimatedMinutes / 60.0);

        double kcalTime = (bmr / 24.0) * MET_RT * effectiveHours;

        // ② 볼륨기반
        final double K = 0.036; // 튜닝 상수 (볼륨→kcal)
        double weightFactor = weight / 70.0;
        double genderFactor = "M".equals(gender) ? 1.05 : 0.95;

        double kcalVol = K * totalVolumeKg * weightFactor * genderFactor;

        // 최종
        int calories = (int) Math.round(Math.max(kcalTime, kcalVol));
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

    public void updateMemo(int elementId, String memoContent){
        routineSqlMapper.deleteMemoByElementId(elementId);
        routineSqlMapper.insertMemo(elementId, memoContent);
    }

}
