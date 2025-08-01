package com.ca.gymbackend.buddy.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.ca.gymbackend.buddy.dto.ChatDto;
import com.ca.gymbackend.buddy.service.BuddyServiceImpl;

@Controller
public class ChatWebSocketController {
    @Autowired
    private BuddyServiceImpl buddyService;

    @MessageMapping("/chat/send") // 클라이언트가 보낼 경로
    @SendTo("/topic/chat/{matchingId}") // 클라이언트가 구독할 경로
    public ChatDto sendMessage(@Payload ChatDto chatDto) {
        chatDto.setSentAt(LocalDateTime.now());
        buddyService.sendChat(chatDto); // DB 저장
        return chatDto;
    }
}
