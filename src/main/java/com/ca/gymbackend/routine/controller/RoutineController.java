package com.ca.gymbackend.routine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
