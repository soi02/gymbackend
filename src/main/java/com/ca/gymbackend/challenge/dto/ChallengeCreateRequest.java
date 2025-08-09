package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;
// import java.util.List;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeCreateRequest {
    private int challengeId;
    private String challengeCreator;
    private String challengeTitle;
    private String challengeDescription;
    private int challengeMaxMembers;
    private int challengeParticipantCount;
    private LocalDate challengeRecruitStartDate; // 모집 기간
    private LocalDate challengeRecruitEndDate; // 모집 기간
    private int challengeDurationDays; // 진행 기간

    
    private MultipartFile challengeThumbnailImage; // 파일 자체
    private String challengeThumbnailPath; // 파일 업로드
    private Integer challengeTendencyId; // 성향
    private Integer challengeDepositAmount; // 보증금

    private List<Integer> keywordIds; // 키워드 ID 목록
}
