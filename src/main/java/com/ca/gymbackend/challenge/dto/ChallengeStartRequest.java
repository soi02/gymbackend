package com.ca.gymbackend.challenge.dto;

import lombok.Data;

@Data
public class ChallengeStartRequest {
    private int userId;
    private int challengeId;
}
