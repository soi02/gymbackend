package com.ca.gymbackend.buddy.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.ca.gymbackend.buddy.dto.ChatDto;
import com.ca.gymbackend.buddy.service.BuddyServiceImpl;

@Controller
public class ChatWebSocketController {
    // @Autowired
    // private BuddyServiceImpl buddyService;

    // // @MessageMapping("/chat/send") // 클라이언트가 보낼 경로
    // // @SendTo("/topic/chat/{matchingId}") // 클라이언트가 구독할 경로
    // // public ChatDto sendMessage(@Payload ChatDto chatDto) {
    // // chatDto.setSentAt(LocalDateTime.now());
    // // buddyService.sendChat(chatDto); // DB 저장
    // // return chatDto;
    // // }
    // @Autowired
    // private SimpMessagingTemplate messagingTemplate;

    // @MessageMapping("/chat/send")
    // public void sendMessage(@Payload ChatDto chatDto) {
    // chatDto.setSentAt(LocalDateTime.now());
    // System.out.println("message 받음"+chatDto.getMessage());
    // buddyService.sendChat(chatDto); // DB 저장

    // // 동적으로 topic 경로 지정해서 브로드캐스트
    // String destination = "/topic/chat/" + chatDto.getMatchingId();
    // messagingTemplate.convertAndSend(destination, chatDto);
    // }

    // @Autowired
    // private SimpMessagingTemplate messagingTemplate; // 특정 목적지로 메시지를 보내는 데 사용됩니다.

    // @Autowired
    // private BuddyServiceImpl buddyService;

    // // 클라이언트가 "/app/chat.sendMessage"로 메시지를 보낼 때 이 메서드가 실행됩니다.
    // @MessageMapping("/chat.sendMessage")
    // public void sendMessage(@Payload ChatDto chatDto) {
    // // 메시지 데이터를 DB에 저장하는 로직을 여기에 추가합니다.
    // buddyService.sendChat(chatDto);

    // // 모든 구독자에게 메시지를 브로드캐스팅합니다.
    // // "/topic/public"을 구독하는 모든 클라이언트에게 chatDto를 보냅니다.
    // // 채팅방마다 주제를 다르게 하고 싶다면, "/topic/chat/{matchingId}"와 같이
    // // matchingId를 사용하여 주제를 동적으로 만들 수 있습니다.
    // messagingTemplate.convertAndSend("/topic/public", chatDto);
    // }

    // // 사용자가 채팅방에 입장했을 때 처리하는 메서드 (옵션)
    // // 클라이언트가 "/app/chat.addUser"로 메시지를 보낼 때 실행됩니다.
    // @MessageMapping("/chat.addUser")
    // public void addUser(@Payload ChatDto chatDto) {
    // // 여기에 사용자 입장 시 필요한 로직을 추가할 수 있습니다.
    // // 예를 들어, "OOO님이 입장하셨습니다." 같은 메시지를
    // // 다른 클라이언트들에게 브로드캐스팅할 수 있습니다.
    // messagingTemplate.convertAndSend("/topic/public", chatDto);
    // }
    // @MessageMapping("/chat.sendMessage") // /app/chat.sendMessage로 프론트가 보냄
    // @SendTo("/topic/public") // 모든 구독자에게 브로드캐스트
    // public ChatDto sendMessage(ChatDto message) {
    // return message;
    // }
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public/{matchingId}")
    public ChatDto sendMessage(ChatDto message) {
        return message;
    }
}
