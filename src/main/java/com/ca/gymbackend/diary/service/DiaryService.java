package com.ca.gymbackend.diary.service;

import java.util.List;
import java.util.Map;

import com.ca.gymbackend.diary.dto.DiaryDto;

public interface DiaryService {
    boolean hasDiaryForToday(int userId);
    void saveDiary(DiaryDto diaryDto);
    List<Map<String, Object>> getDiariesByUserId(int userId);
    List<Map<String, Object>> getAllEmojis();
    Map<String, Object> getDiaryByUserIdAndDate(int userId, String targetDate);
}
