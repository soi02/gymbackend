package com.ca.gymbackend.buddy.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatDto {
    private int id;
    private int matchingId;
    private int sendBuddyId;
    private String message;
    private boolean isRead;
    private LocalDateTime sentAt;
}
