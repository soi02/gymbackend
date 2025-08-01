package com.ca.gymbackend.market.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MarketReviewOnUserDto {
    private int id;
    private int writerId;
    private int evaluatedUserId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}