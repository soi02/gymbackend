package com.ca.gymbackend.diary.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.diary.dto.DiaryDto;
import com.ca.gymbackend.diary.mapper.DiarySqlMapper;
import com.ca.gymbackend.diary.mapper.EmojiSqlMapper;

@Service
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private DiarySqlMapper diarySqlMapper;

    @Autowired
    private EmojiSqlMapper emojiSqlMapper;

    @Override
    public boolean hasDiaryForToday(int userId) {
        Integer result = diarySqlMapper.hasDiaryForToday(userId);
        return result != null && result > 0;
    }

    @Override
    public void saveDiary(DiaryDto diaryDto) {
        diarySqlMapper.insertDiary(diaryDto);
    }

    @Override
    public List<Map<String, Object>> getDiariesByUserId(int userId) {
        return diarySqlMapper.getDiariesByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getAllEmojis() {
        return emojiSqlMapper.getAllEmojis();
    }

    @Override
    public Map<String, Object> getDiaryByUserIdAndDate(int userId, String targetDate) {
        return diarySqlMapper.getDiaryByUserIdAndDate(userId, targetDate);
    }
}
