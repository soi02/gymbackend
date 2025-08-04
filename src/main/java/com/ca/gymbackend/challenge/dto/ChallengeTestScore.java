package com.ca.gymbackend.challenge.dto;

import lombok.Data;

@Data
public class ChallengeTestScore {
    private Integer testScoreId; // insert 후 자동 생성된 키를 받기 위해 필요
    private Integer userId;
    private Integer goalOriented;
    private Integer relationshipOriented;
    private Integer recoveryOriented;
    private Integer learningOriented;
    private Integer balanced;
}
