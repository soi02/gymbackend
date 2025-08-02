package com.ca.gymbackend.challenge.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChallengeUserInfo {
    private LocalDateTime personalJoinDate;
    private LocalDateTime personalEndDate;
}
