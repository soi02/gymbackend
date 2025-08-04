// src/main/java/com/ca/gymbackend/buddy/controller/ChatWebSocketController.java
package com.ca.gymbackend.buddy.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.ca.gymbackend.buddy.dto.ChatDto;
import com.ca.gymbackend.buddy.service.BuddyServiceImpl;

@Controller // WebSocket 메시지 처리를 위한 컨트롤러
public class ChatWebSocketController {

    @Autowired
    private BuddyServiceImpl buddyService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 클라이언트가 '/app/chat/send'로 메시지를 보내면 이 메서드가 호출됩니다.
    @MessageMapping("/chat/send")
    public void sendMessage(ChatDto chatDto) {
        System.out.println("웹소켓으로 받은 메시지: " + chatDto.getMessage());

        // 1. 받은 메시지를 DB에 저장
        buddyService.sendChat(chatDto);

        // 2. 해당 채팅방을 구독하고 있는 모든 클라이언트에게 메시지 브로드캐스팅
        messagingTemplate.convertAndSend("/topic/" + chatDto.getMatchingId(), chatDto);
    }
}