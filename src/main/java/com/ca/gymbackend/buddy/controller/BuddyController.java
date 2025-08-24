package com.ca.gymbackend.buddy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String token = request.getHeader("Authorization").substring(7);
        Integer userId = jwtUtil.getUserId(token);

        System.out.println("Before set: " + buddyDto.getUserId());
        buddyDto.setUserId(userId);
        System.out.println("After set: " + buddyDto.getUserId());

        buddyService.registerBuddy(buddyDto);
        buddyService.updateIsBuddy(buddyDto.getUserId());

        return "버디 등록 완료";
    }

    // 버디인지 확인
    @GetMapping("/is-buddy")
    public ResponseEntity<Map<String, Boolean>> isBuddyStatus(@RequestParam("userId") int userId) {
        boolean isBuddy = buddyService.isBuddy(userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("is_buddy", isBuddy);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getBuddyUserList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        
        if (principal.equals("anonymousUser")) {
            return ResponseEntity.status(401).body(null); // 인증되지 않은 사용자
        }
        
        int loggedInUserId = Integer.parseInt(principal.toString());
        List<Map<String, Object>> result = buddyService.getBuddyUserList(loggedInUserId);
        return ResponseEntity.ok(result);
    }

    // 채팅룸 리스트 나열
    @GetMapping("/rooms/{buddyId}")
    public List<ChatRoomDto> getChatRooms(@PathVariable("buddyId") int buddyId) {
        return buddyService.findChatRoomsByBuddyId(buddyId);
    }

    // 매칭 요청 보내기 (하트 누르기)
    @PostMapping("/request")
    public ResponseEntity<?> sendMatchingRequest(@RequestBody MatchingDto dto) {
        try {
            System.out.println("====================");
            System.out.println("받은 MatchingDto: " + dto);
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
    public List<Map<String, Object>> getMatchingNotifications(@PathVariable("buddyId") int buddyId) {
        return buddyService.getMatchingNotifications(buddyId);
    }

    @GetMapping("/matching-info/{matchingId}")
    public ResponseEntity<Map<String, Object>> getMatchingInfo(@PathVariable("matchingId") int matchingId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int loggedInUserId = Integer.parseInt(authentication.getPrincipal().toString());

            Map<String, Object> matchingInfo = buddyService.getMatchingInfo(matchingId, loggedInUserId);

            if (matchingInfo != null) {
                return ResponseEntity.ok(matchingInfo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 채팅 관련 API
    @PostMapping("/send")
    public ResponseEntity<?> sendChat(@RequestBody ChatDto chatDto) {
        try {
            // ⭐ 추가: messagingTemplate이 null인지 확인하는 로그
            if (messagingTemplate == null) {
                System.err.println("❌ messagingTemplate이 주입되지 않았습니다!");
            } else {
                System.out.println("✅ messagingTemplate이 정상적으로 주입되었습니다.");
            }

            // ⭐ 수정: Service에서 ChatDto를 반환하도록 변경했으므로, 반환값을 받음
            ChatDto savedChat = buddyService.sendChat(chatDto);

            // ⭐ 추가: 메시지를 DB에 저장한 후, 웹소켓으로 해당 채팅방에 브로드캐스팅
            messagingTemplate.convertAndSend("/topic/" + savedChat.getMatchingId(), savedChat);

            return ResponseEntity.ok("메시지 전송 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("메시지 전송 실패: " + e.getMessage());
        }
    }

    // 특정 매칭 채팅 목록 조회
    @GetMapping("/list/{matchingId}")
    public ResponseEntity<List<ChatDto>> getChatsByMatchingId(@PathVariable(value = "matchingId") int matchingId) {
        List<ChatDto> chats = buddyService.getChatsByMatchingId(matchingId);
        return ResponseEntity.ok(chats);
    }

    // 채팅방 입장 시 메시지를 읽음 처리
    @PostMapping("/chat/read/{matchingId}")
    public ResponseEntity<Void> markChatsAsRead(@PathVariable(value = "matchingId") int matchingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int readerBuddyId = Integer.parseInt(authentication.getPrincipal().toString());

        // ⭐ 수정: 읽음 처리된 메시지들의 ID 목록을 받음
        List<Integer> readChatIds = buddyService.markMessagesAsRead(matchingId, readerBuddyId);

        // ⭐ 추가: 읽음 처리 정보 브로드캐스팅
        if (!readChatIds.isEmpty()) {
            Map<String, Object> readStatusUpdate = new HashMap<>();
            readStatusUpdate.put("type", "READ_STATUS");
            readStatusUpdate.put("matchingId", matchingId);
            readStatusUpdate.put("readerId", readerBuddyId);
            readStatusUpdate.put("readChatIds", readChatIds);

            messagingTemplate.convertAndSend("/topic/" + matchingId, readStatusUpdate);
        }

        return ResponseEntity.ok().build();
    }
}