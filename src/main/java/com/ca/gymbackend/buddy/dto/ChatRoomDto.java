package com.ca.gymbackend.buddy.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatRoomDto {
    private int matchingId;
    private String opponentName;
    private String opponentProfileImage;
    private String opponentIntro;
    private String lastMessage;
    private LocalDateTime lastSentAt;
    private int unreadCount;
}
