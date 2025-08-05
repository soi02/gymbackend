package com.ca.gymbackend.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.challenge.dto.groupchat.GroupChatMessage;
import com.ca.gymbackend.challenge.service.GroupChatMessageServiceImpl;

@RestController
@RequestMapping("/api/challenge/groupchat")
public class GroupChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private GroupChatMessageServiceImpl groupChatMessageService;

    // 챌린지 채팅방 접속 시, 이전 메시지 기록을 불러오는 HTTP API
    @GetMapping("/getChatHistoryProcess/{challengeId}")
    public ResponseEntity<List<GroupChatMessage>> getChatHistoryProcess(@PathVariable("challengeId") Long challengeId) {
        System.out.println("getChatHistoryProcess 메서드 호출. challengeId: " + challengeId);
        List<GroupChatMessage> groupChatMessage = groupChatMessageService.getAllMessagesByChallengeId(challengeId);
        System.out.println("챌린지 ID " + challengeId + "에 대한 채팅 기록 " + groupChatMessage.size() + "건 조회 성공");
        return ResponseEntity.ok(groupChatMessage);
    }

    // 클라이언트가 '/app/sendGroupMessage/{challengeId}'로 메시지를 보내면 이 메서드가 호출됨
    @MessageMapping("/sendGroupMessage/{challengeId}")
    public void sendGroupMessage(@DestinationVariable Long challengeId,
                                 @Payload GroupChatMessage groupChatMessage) {

        System.out.println("STOMP 메시지 수신: destination=/app/sendGroupMessage/" + challengeId + ", payload=" + groupChatMessage);

        Long userId = groupChatMessage.getSenderUserId(); // DTO에서 userId를 가져옵니다.

        if (userId == null) {
            System.out.println("인증된 사용자 ID가 없어 메시지 처리를 중단합니다.");
            return;
        }

        try {
            // 메시지에 전송 시각, 챌린지 ID 설정
            groupChatMessage.setChallengeId(challengeId);
            // userId는 이미 DTO에 있으므로 별도로 설정하지 않아도 됩니다.

            System.out.println("메시지 DB 저장 시작. 챌린지 ID: " + challengeId + ", 발신자 ID: " + userId);
            
            // 1. 메시지를 데이터베이스에 저장
            GroupChatMessage savedMessage = groupChatMessageService.saveMessage(groupChatMessage);
            
            System.out.println("메시지 DB 저장 성공. 저장된 메시지 ID: " + savedMessage.getGroupChatMessageId());
            
            // 2. '/topic-group' 브로커를 통해 메시지 전송
            String destination = "/topic-group/sendGroupMessage/" + challengeId;
            System.out.println("메시지 브로드캐스트 시작. 대상: " + destination);
            
            simpMessagingTemplate.convertAndSend(destination, savedMessage);
            
            System.out.println("메시지 브로드캐스트 완료");

        } catch (Exception e) {
            System.err.println("메시지 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}