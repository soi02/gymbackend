package com.ca.gymbackend.challenge.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeMyRecordDetailResponse {
    private ChallengeInfo challengeInfo;
    private List<ChallengeRecordInfo> challengeRecordInfoList;
}

// @Data 만 써주면, 기본 생성자가 사라진다. 그래서 @NoArgsConstructor 를 같이 써줘야 한다
// + 모든 필드를 받는 생성자도 써줘야 한다. @AllArgsConstructor 도 써주기
