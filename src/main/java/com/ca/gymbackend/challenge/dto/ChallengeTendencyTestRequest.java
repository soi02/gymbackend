package com.ca.gymbackend.challenge.dto;

import java.util.List;

import lombok.Data;

@Data
public class ChallengeTendencyTestRequest {
    private int userId;
    private List<Integer> keywordIds;
}
