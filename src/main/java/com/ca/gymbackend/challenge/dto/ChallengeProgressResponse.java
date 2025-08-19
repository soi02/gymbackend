package com.ca.gymbackend.challenge.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChallengeProgressResponse {
    private String challengeTitle;
    private int totalPeriod;
    // private int norigaeConditionRate;
    // private String norigaeName;
    // private String norigaeIconPath;
    private int myAchievement;

    private List<ChallengeAttendanceStatus> challengeAttendanceStatus;

    // 이미 획득한 노리개 중 가장 높은 등급의 정보
    private String awardedNorigaeName;
    private String awardedNorigaeIconPath;

    // 이번 출석으로 새로 획득한 노리개 등급 ID
    private Integer newlyAwardedNorigaeTierId;

    // 새로 추가할 필드: 획득한 모든 노리개 목록**
    private List<ChallengeNorigaeAwardInfo> awardedNorigaeList; 

    private String challengeThumbnailPath;
}
