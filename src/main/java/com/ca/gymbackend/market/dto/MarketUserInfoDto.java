package com.ca.gymbackend.market.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MarketUserInfoDto {
    private int id;
    private int userId;
    private String nickname;
    private LocalDateTime createdAt;
}
