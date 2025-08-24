package com.ca.gymbackend.challenge.dto;

import java.sql.Date;
import java.util.List;

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

    private int challengeParticipantCount; // 참가 인원


    // ✅ 추가: 도넛/통계용
    private Integer categoryId;        // keyword_category_id
    private String categoryName;       // keyword_category_name (예: 루틴/회복/…)

    // ✅ 핵심 수정: 일별 인증 기록을 담을 리스트 추가
    private List<String> daysAttendedList; 

}
