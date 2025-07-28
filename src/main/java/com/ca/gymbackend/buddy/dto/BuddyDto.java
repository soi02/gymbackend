package com.ca.gymbackend.buddy.dto;

import java.util.List;

import lombok.Data;

@Data
public class BuddyDto {
    private int id;
    private int userId;
    private List<AgeDto> buddyAgeList;
    private String preferredGender;
    private String intro;
}
