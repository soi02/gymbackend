package com.ca.gymbackend.buddy.dto;

import lombok.Data;

@Data
public class MatchingDto {
    private int id;
    private int sendBuddyId;;
    private int receiverBuddyId;
    private String status; // "pending", "accepted", "rejected"
}
