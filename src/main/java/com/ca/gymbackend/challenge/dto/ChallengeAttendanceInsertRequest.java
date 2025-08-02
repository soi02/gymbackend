package com.ca.gymbackend.challenge.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ChallengeAttendanceInsertRequest {
    private int userId;
    private int challengeId;
    private MultipartFile photo;
}
