package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ChallengeAttendanceStatus {
    private LocalDate recordDate;
    private String status; // 인증완료, 결석, 미래
    private String photoUrl;
}
