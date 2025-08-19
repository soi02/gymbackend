package com.ca.gymbackend.diary.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.diary.dto.DiaryDto;

@Mapper
public interface DiarySqlMapper {
    Integer hasDiaryForToday(@Param("userId") int userId);
    void insertDiary(DiaryDto diaryDto);
    List<Map<String, Object>> getDiariesByUserId(@Param("userId") int userId);
    Map<String, Object> getDiaryByUserIdAndDate(@Param("userId") int userId, @Param("targetDate") String targetDate);
}
