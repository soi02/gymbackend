package com.ca.gymbackend.routine.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.routine.request.ActualWorkoutSaveRequest;
import com.ca.gymbackend.routine.request.RoutineSaveRequest;
import com.ca.gymbackend.routine.response.ActualWorkoutResultResponse;
import com.ca.gymbackend.routine.response.RoutineDetailResponse;
import com.ca.gymbackend.routine.service.RoutineService;

@RestController
@RequestMapping("/api/routine")

public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @GetMapping("/getArticleList")
    public ResponseEntity<?> getArticleList() {
        return ResponseEntity.ok(routineService.getArticleList());
    }

    @GetMapping("/getWorkoutGuide/{id}")
    public ResponseEntity<?> getWorkoutGuide(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(routineService.getWorkoutGuide(id));
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("서버 에러: " + e.getMessage());
        }
    }

    @PostMapping("/saveRoutine")
    public ResponseEntity<?> saveRoutine(@RequestBody RoutineSaveRequest request) {
            System.out.println("🔥 saveRoutine() 진입!");
            System.out.println("📦 받은 요청: " + request);
    
        try {
            routineService.saveRoutine(request);
            return ResponseEntity.ok("루틴 저장 완료");
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("루틴 저장 실패: " + e.getMessage());
        }

    }

    @GetMapping("/getRoutinesByUserId/{userId}")
    public ResponseEntity<?> getRoutinesByUserId(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(routineService.getRoutinesByUserId(userId));
    }
    
    @GetMapping("/list/{routineId}")
    public RoutineDetailResponse getRoutineDetail(@PathVariable("routineId") int routineId) {
        return routineService.getRoutineDetail(routineId);
    }

    @GetMapping("/routineSets/{routineId}")
    public RoutineDetailResponse getFullRoutineDetail(@PathVariable("routineId") int routineId) {
        return routineService.getRoutineDetail(routineId);
    }

    @PostMapping("/saveActualWorkout")
    public ResponseEntity<?> saveActualWorkout(@RequestBody ActualWorkoutSaveRequest request) {
        try {
            int workoutId = routineService.saveActualWorkout(request);
           return ResponseEntity.ok(Map.of("workoutId", workoutId)); // ✅ 이렇게 반환!
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("기록 저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/result/{workoutId}")
    public ResponseEntity<?> getWorkoutResult(@PathVariable("workoutId") int workoutId) {
        try {
            List<ActualWorkoutResultResponse> result = routineService.getWorkoutResult(workoutId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("결과 조회 실패: " + e.getMessage());
        }
    }
    
    @GetMapping("/getWorkoutByDate")
    public ResponseEntity<?> getWorkoutByDate(
        @RequestParam("userId") int userId,
        @RequestParam("date") String date
    ) {
        try {
            List<ActualWorkoutResultResponse> result = routineService.getWorkoutByDate(userId, date);
            return ResponseEntity.ok(result);

        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("결과 조회 실패: " + e.getMessage());
        }

    }



}
