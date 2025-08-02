package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ChallengeAttendanceRecord {
    private LocalDate attendanceDate;
    private String attendanceImagePath;
}
