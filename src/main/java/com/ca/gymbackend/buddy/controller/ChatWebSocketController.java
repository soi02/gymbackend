// src/main/java/com/ca/gymbackend/buddy/controller/ChatWebSocketController.java
package com.ca.gymbackend.buddy.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
    public void sendMessage(@Payload ChatDto chatDto) {
        System.out.println("웹소켓으로 받은 메시지: " + chatDto.getMessage());

        // 1. 받은 메시지를 DB에 저장
        buddyService.sendChat(chatDto);

        // 2. 해당 채팅방을 구독하고 있는 모든 클라이언트에게 메시지 브로드캐스팅
        messagingTemplate.convertAndSend("/topic/" + chatDto.getMatchingId(), chatDto);
    }


    // ✅ WebRTC 시그널링 메시지를 처리하는 새로운 메서드 추가 및 수정
    // 클라이언트가 '/app/webrtc/{callId}'로 메시지를 보내면 이 메서드가 호출됩니다.
    // @MessageMapping("/webrtc/{callId}")
    // public void handleWebRtcSignal(@DestinationVariable("callId") String callId,@Payload String signalMessage) {
    //     System.out.println("웹소켓으로 받은 WebRTC 시그널링 메시지: " + signalMessage);
    //     System.out.println("WebRTC 메시지 전송 대상 callId: " + callId);
        
    //     // 클라이언트에서 보낸 메시지를 그대로 다시 해당 토픽으로 브로드캐스팅합니다.
    //     // 클라이언트의 `WebRtcCall.jsx`에서 `subscribe` 경로를 `/topic/webrtc/{callId}`로 설정했으므로
    //     // 이 경로로 메시지를 전달해야 합니다.
    //     messagingTemplate.convertAndSend("/topic/webrtc/" + callId, signalMessage);
    // }

    /**
     * 클라이언트가 `/app/webrtc/{matchingId}`로 웹RTC 시그널링 메시지를 보내면 호출됩니다.
     * 받은 메시지를 해당 채팅방을 구독하는 모든 클라이언트에게 그대로 전달합니다.
     */
    @MessageMapping("/webrtc/{matchingId}")
    public void handleWebRtcSignaling(@DestinationVariable("matchingId") String matchingId, @Payload String message) {
        System.out.println("웹소켓으로 받은 웹RTC 시그널링 메시지: " + message);
        messagingTemplate.convertAndSend("/topic/webrtc/" + matchingId, message);
    }

}