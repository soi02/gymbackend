package com.ca.gymbackend.market.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MarketDealedLogDto {
    private int id;
    private int sellerId;
    private int buyerId;
    private int specificArticleId;
    private LocalDateTime createdAt;
}