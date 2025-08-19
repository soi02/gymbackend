package com.ca.gymbackend.diary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ca.gymbackend.diary.dto.DiaryDto;
import com.ca.gymbackend.diary.service.DiaryService;
import com.ca.gymbackend.security.JwtUtil;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/check-today")
    public ResponseEntity<Boolean> hasDiaryForToday(@RequestParam("userId") int userId) {
        boolean hasDiary = diaryService.hasDiaryForToday(userId);
        return ResponseEntity.ok(hasDiary);
    }

    @PostMapping("/write")
    public ResponseEntity<Void> saveDiary(@RequestBody DiaryDto diaryDto, @RequestHeader("Authorization") String token) {
        // JWT 토큰에서 userId 추출
        String jwtToken = token.replace("Bearer ", "");
        Integer userId = jwtUtil.getUserId(jwtToken);
        diaryDto.setUser_id(userId);
        
        diaryService.saveDiary(diaryDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getDiariesByUserId(@RequestParam("userId") int userId) {
        List<Map<String, Object>> diaries = diaryService.getDiariesByUserId(userId);
        return ResponseEntity.ok(diaries);
    }

    @GetMapping("/date")
    public ResponseEntity<?> getDiaryByDate(
            @RequestParam("userId") int userId,
            @RequestParam("date") String targetDate) {
        Map<String, Object> diary = diaryService.getDiaryByUserIdAndDate(userId, targetDate);
        if (diary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(diary);
    }

    @GetMapping("/emojis")
    public ResponseEntity<List<Map<String, Object>>> getAllEmojis() {
        List<Map<String, Object>> emojis = diaryService.getAllEmojis();
        return ResponseEntity.ok(emojis);
    }
}
