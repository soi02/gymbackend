package com.ca.gymbackend.diary.dto;

import java.util.Date;
import lombok.Data;

@Data
public class DiaryDto {
    private int id;
    private int user_id;
    private int emoji_id;
    private String content;
    private Date created_at;
}
