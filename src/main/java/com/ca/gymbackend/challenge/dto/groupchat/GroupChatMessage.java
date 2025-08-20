package com.ca.gymbackend.challenge.dto.groupchat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // ✅ 이 import 문이 있는지 확인
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // ✅ 이 코드가 있는지 확인
public class GroupChatMessage {
    private String challengeTitle;
    private Long groupChatMessageId;
    private Long challengeId;
    private Long senderUserId;
    private String groupChatMessageContent;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    // 👇 이 필드들을 추가합니다.
    private String senderProfileImagePath;
    private String senderNickname;

        // 👇 읽음 수 필드 추가
    private Long readCount;

    

    
}