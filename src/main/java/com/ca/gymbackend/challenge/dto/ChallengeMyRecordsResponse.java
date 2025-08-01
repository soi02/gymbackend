package com.ca.gymbackend.challenge.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class ChallengeMyRecordsResponse {
    private int challengeId;
    private String challengeTitle;
    private String challengeThumbnailPath;
    private int challengeDurationDays;
    private Date personalJoinDate;
    private int daysAttended; // 총 출석일수
    private boolean todayAttended; // 오늘 출석 여부

}
