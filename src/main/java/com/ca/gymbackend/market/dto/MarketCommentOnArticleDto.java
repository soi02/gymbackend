package com.ca.gymbackend.market.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MarketCommentOnArticleDto {
    private int id;
    private int articleId;
    private int marketUserId;
    private int commentOfComment;
    private int targetCommentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}