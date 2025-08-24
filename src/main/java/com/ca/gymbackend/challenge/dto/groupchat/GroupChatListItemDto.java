package com.ca.gymbackend.challenge.dto.groupchat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class GroupChatListItemDto {
    private Long challengeId;
    private String challengeTitle;
    private String challengeThumbnailPath;

    private Integer challengeParticipantCount;  // 참여 인원
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime personalJoinDate;     // 사용자의 해당 챌린지 참여일

    // ✨ 새로 내려줄 필드
    private String lastMessage;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
}
