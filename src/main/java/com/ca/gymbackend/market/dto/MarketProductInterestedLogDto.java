package com.ca.gymbackend.market.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MarketProductInterestedLogDto {
    private int id;
    private int marketUserId;
    private int specificArticleId;
    private LocalDateTime createdAt;
}
