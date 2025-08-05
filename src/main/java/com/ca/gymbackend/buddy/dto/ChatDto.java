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
    // 상대방정보 담을 필드 추가
    private String senderName;
    private String senderProfileImageUrl;
}
