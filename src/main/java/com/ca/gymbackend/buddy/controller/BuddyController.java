package com.ca.gymbackend.buddy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.buddy.dto.BuddyDto;
import com.ca.gymbackend.buddy.dto.ChatDto;
import com.ca.gymbackend.buddy.dto.ChatRoomDto;
import com.ca.gymbackend.buddy.dto.MatchingDto;
import com.ca.gymbackend.buddy.service.BuddyServiceImpl;
import com.ca.gymbackend.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/buddy")
public class BuddyController {

    @Autowired
    private BuddyServiceImpl buddyService;
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // 버디 등록
    @PostMapping("/register")
    public String registerBuddy(@RequestBody BuddyDto buddyDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // "Bearer " 제거
        Integer userId = jwtUtil.getUserId(token); // JwtUtil에 이미 존재함

        System.out.println("Before set: " + buddyDto.getUserId());
        buddyDto.setUserId(userId);
        System.out.println("After set: " + buddyDto.getUserId());

        buddyService.registerBuddy(buddyDto);
        buddyService.updateIsBuddy(buddyDto.getUserId());

        // System.out.println("넘어온 buddyDto: " + buddyDto);
        // System.out.println("buddyAgeList: " + buddyDto.getBuddyAgeList());
        return "버디 등록 완료";
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getBuddyUserList() {
        List<Map<String, Object>> result = buddyService.getBuddyUserList();
        return ResponseEntity.ok(result);
    }
    // 채팅룸 리스트 나열
    @GetMapping("/rooms/{buddyId}")
    public List<ChatRoomDto> getChatRooms(@PathVariable int buddyId) {
        return buddyService.findChatRoomsByBuddyId(buddyId);
    }

    // 매칭 요청 보내기 (하트 누르기)
    @PostMapping("/request")
    public ResponseEntity<?> sendMatchingRequest(@RequestBody MatchingDto dto) {
        try {
            System.out.println("====================");
            System.out.println("받은 MatchingDto: " + dto); // <-- 이게 null이면 문제는 프론트 or DTO
            System.out.println("senderId: " + dto.getSendBuddyId());
            System.out.println("receiverId: " + dto.getReceiverBuddyId());
            buddyService.sendMatchingRequest(dto.getSendBuddyId(), dto.getReceiverBuddyId());
            System.out.println(dto.getReceiverBuddyId());
            return ResponseEntity.ok("매칭 요청 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("매칭 요청 실패: " + e.getMessage());
        }
    }

   @PostMapping("/response")
    public ResponseEntity<?> respondToMatching(@RequestBody MatchingDto dto) {
        try {
            buddyService.respondToMatching(dto.getId(), dto.getStatus(), dto.getSendBuddyId());

            if ("수락".equals(dto.getStatus())) {
                buddyService.insertInitialChat(dto.getId(), dto.getSendBuddyId());

                // 웹소켓으로 초기 메시지 전송
                ChatDto initialChat = new ChatDto();
                initialChat.setMatchingId(dto.getId());
                initialChat.setSendBuddyId(dto.getSendBuddyId()); 
                initialChat.setMessage("버디 매칭이 완료되었습니다! 첫 인사를 나눠보세요.");
                
                messagingTemplate.convertAndSend("/topic/" + dto.getId(), initialChat);
            }

            return ResponseEntity.ok("응답 처리 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("응답 실패: " + e.getMessage());
        }
    }

    @GetMapping("/matching-notifications/{buddyId}")
    public List<Map<String, Object>> getMatchingNotifications(@PathVariable int buddyId) {
        return buddyService.getMatchingNotifications(buddyId);
    }

    // 채팅 관련 API
    @PostMapping("/send")
    public ResponseEntity<?> sendChat(@RequestBody ChatDto chatDto) {
        try {
            buddyService.sendChat(chatDto);
            return ResponseEntity.ok("메시지 전송 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("메시지 전송 실패: " + e.getMessage());
        }
    }

    // 특정 매칭 채팅 목록 조회
    @GetMapping("/list/{matchingId}")
    public ResponseEntity<List<ChatDto>> getChatsByMatchingId(@PathVariable int matchingId) {
        List<ChatDto> chats = buddyService.getChatsByMatchingId(matchingId);
        return ResponseEntity.ok(chats);
    }

    // 메시지 읽음 처리 (옵션)
    @PostMapping("/read/{chatId}")
    public ResponseEntity<?> markChatAsRead(@PathVariable int chatId) {
        try {
            buddyService.markChatAsRead(chatId);
            return ResponseEntity.ok("읽음 처리 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("읽음 처리 실패: " + e.getMessage());
        }
    }
}
