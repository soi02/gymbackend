package com.ca.gymbackend.challenge.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChallengeFinalTestResult {
    private int finalResultId;
    private int testScoreId; // test_score 테이블과 연관이 있다면 사용
    private int userId;
    private String topType1;
    private int topScore1;
    private String topType2;
    private int topScore2;
    private String testText;
    private LocalDateTime createdAt;
}
