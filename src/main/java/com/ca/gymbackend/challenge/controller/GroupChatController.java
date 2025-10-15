package com.ca.gymbackend.challenge.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.challenge.dto.groupchat.GroupChatListItemDto;
import com.ca.gymbackend.challenge.dto.groupchat.GroupChatMessage;
import com.ca.gymbackend.challenge.service.GroupChatMessageServiceImpl;

@RestController
@RequestMapping("/api/challenge/groupchat")
public class GroupChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private GroupChatMessageServiceImpl groupChatMessageService;



    @PostMapping("/readMessageProcess")
    public ResponseEntity<Void> readMessageProcess(@RequestBody Map<String, Object> payload) {
        List<Integer> messageIds = (List<Integer>) payload.get("messageIds");
        Long userId = ((Number) payload.get("userId")).longValue(); // userId를 Long 타입으로 변환
        
        System.out.println("readMessageProcess 호출. userId: " + userId + ", messageIds: " + messageIds);
        
        for (Integer messageIdInt : messageIds) {
            Long messageId = messageIdInt.longValue();
            
            // 1. 메시지 읽음 상태를 DB에 저장
            groupChatMessageService.saveMessageReadStatus(messageId, userId);
            
            // 2. 해당 메시지의 챌린지 ID를 조회
            Long challengeId = groupChatMessageService.findChallengeIdByMessageId(messageId);
            
            // 3. 메시지 ID에 해당하는 읽은 사람 수 조회
            Long readCount = groupChatMessageService.getReadCountByMessageId(messageId);
            
            // 4. 읽음 수 업데이트를 모든 클라이언트에게 브로드캐스트
            simpMessagingTemplate.convertAndSend(
                "/topic-group/readMessageUpdate/" + challengeId, 
                Map.of("messageId", messageId, "readCount", readCount)
            );
        }
        
        return ResponseEntity.ok().build();
    }

// 챌린지 채팅방 접속 시, 이전 메시지 기록을 불러오는 HTTP API
@GetMapping("/getChatHistoryProcess/{challengeId}")
public ResponseEntity<List<GroupChatMessage>> getChatHistoryProcess(@PathVariable("challengeId") Long challengeId) {
    System.out.println("getChatHistoryProcess 메서드 호출. challengeId: " + challengeId);

    // 1. 챌린지 제목을 가져오는 로직 추가
    String challengeTitle = groupChatMessageService.getChallengeTitleById(challengeId); 

    // 2. 기존 메시지 기록을 가져옵니다.
    List<GroupChatMessage> groupChatMessages = groupChatMessageService.getAllMessagesByChallengeId(challengeId);

    // 3. 각 메시지 DTO에 readCount와 함께 챌린지 제목을 설정합니다.
    for (GroupChatMessage message : groupChatMessages) {
        Long readCount = groupChatMessageService.getReadCountByMessageId(message.getGroupChatMessageId());
        message.setReadCount(readCount);
        message.setChallengeTitle(challengeTitle);
    }

    System.out.println("챌린지 ID " + challengeId + "에 대한 채팅 기록 " + groupChatMessages.size() + "건 조회 성공");
    return ResponseEntity.ok(groupChatMessages);
}

// 챌린지 참여 인원 수를 반환하는 HTTP API
    @GetMapping("/getParticipantCount/{challengeId}")
    public ResponseEntity<Integer> getParticipantCount(@PathVariable("challengeId") Long challengeId) {
        System.out.println("getParticipantCount 메서드 호출. challengeId: " + challengeId);
        int participantCount = groupChatMessageService.getParticipantCountByChallengeId(challengeId);
        System.out.println("챌린지 ID " + challengeId + "의 참여 인원: " + participantCount + "명");
        return ResponseEntity.ok(participantCount);
    }
    

    




    // 클라이언트가 '/app/sendGroupMessage/{challengeId}'로 메시지를 보내면 이 메서드가 호출됨
    @MessageMapping("/sendGroupMessage/{challengeId}")
    public void sendGroupMessage(@DestinationVariable("challengeId") Long challengeId,
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

            System.out.println("메시지 DB 저장 시작. 챌린지 ID: " + challengeId + ", 발신자 ID: " + userId);
            
            // 1. 메시지를 데이터베이스에 저장
            GroupChatMessage savedMessage = groupChatMessageService.saveMessage(groupChatMessage);
            
            System.out.println("메시지 DB 저장 성공. 저장된 메시지 ID: " + savedMessage.getGroupChatMessageId());
            
            // 👇 추가된 로직: 메시지를 보낸 사람을 첫 번째 읽은 사람으로 처리
            groupChatMessageService.saveMessageReadStatus(savedMessage.getGroupChatMessageId(), savedMessage.getSenderUserId());
            
            // 👇 추가된 로직: 브로드캐스트할 메시지 객체에 읽음 수를 1로 설정
            savedMessage.setReadCount(1L);
            
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



        @GetMapping("/listWithSummary/{userId}")
        public ResponseEntity<List<GroupChatListItemDto>> listWithSummary(@PathVariable("userId") Long userId) {
        List<GroupChatListItemDto> list = groupChatMessageService.getJoinedChallengesWithChatSummary(userId);
        return ResponseEntity.ok(list);
    }



}