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
    // ✅ 이 필드를 추가해 주세요.
    private int unreadCount;
}
