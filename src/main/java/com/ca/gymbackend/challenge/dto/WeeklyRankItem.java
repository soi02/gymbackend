package com.ca.gymbackend.challenge.dto;

import lombok.Data;

@Data
public class WeeklyRankItem {
    private int userId;
    private String userName;          // user.account_name
    private String profileImagePath;  // user.profile_image
    private int distinctDaysThisWeek;
    private int progressPercent;
    private int rank;
}