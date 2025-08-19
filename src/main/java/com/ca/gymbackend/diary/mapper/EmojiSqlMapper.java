package com.ca.gymbackend.diary.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmojiSqlMapper {
    List<Map<String, Object>> getAllEmojis();
}
