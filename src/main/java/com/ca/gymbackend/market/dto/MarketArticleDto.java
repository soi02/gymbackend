package com.ca.gymbackend.market.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MarketArticleDto {
    private int id;
    private int marketUserId;
    private String imageLink;
    private String imageOriginalFilename;
    private int mainImageId;
    private String title;
    private String content;
    private int productCostOption;
    private int productCost;
    private int viewedCount;
    private int sellEnded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}