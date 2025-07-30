package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;
import java.util.List;


import lombok.Data;

@Data
public class ChallengeCreateRequest {
    private int challengeId;
    private String challengeCreator;
    private String challengeTitle;
    private String challengeDescription;
    private int challengeMaxMembers;
    private LocalDate challengeStartDate;
    private LocalDate challengeEndDate;
    private List<Integer> challengeKeywordIds;


    private String challengeThumnailPath; // 파일 저장 경로
}
