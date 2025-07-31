package com.ca.gymbackend.routine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.routine.request.RoutineSaveRequest;
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
        try {
            routineService.saveRoutine(request);
            return ResponseEntity.ok("루틴 저장 완료");
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("루틴 저장 실패: " + e.getMessage());
        }

    }
    

}
