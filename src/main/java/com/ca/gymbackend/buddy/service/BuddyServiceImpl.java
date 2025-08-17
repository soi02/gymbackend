package com.ca.gymbackend.buddy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ca.gymbackend.buddy.dto.AgeDto;
import com.ca.gymbackend.buddy.dto.BuddyDto;
import com.ca.gymbackend.buddy.dto.ChatDto;
import com.ca.gymbackend.buddy.dto.ChatRoomDto;
import com.ca.gymbackend.buddy.mapper.BuddySqlMapper;

@Service
public class BuddyServiceImpl {
    @Autowired
    private BuddySqlMapper buddyMapper;

    // BuddyServiceImpl.java 의 registerBuddy 메소드
    public void registerBuddy(BuddyDto buddyDto) {
        // 1. 처음 넘어온 buddyDto.getBuddyAgeList() 확인
        System.out.println("초기 buddyDto.buddyAgeList: " + buddyDto.getBuddyAgeList());

        List<AgeDto> filledAgeList = new ArrayList<>();
        for (AgeDto age : buddyDto.getBuddyAgeList()) {
            // 2. findByAgeId 호출 전 age.getId() 값 확인
            System.out.println("findByAgeId 호출 전 age.getId(): " + age.getId());

            AgeDto fullAge = buddyMapper.findByAgeId(age.getId());

            // 3. findByAgeId 호출 후 fullAge 객체의 id, age 값 확인
            System.out.println("findByAgeId 호출 후 fullAge: " + fullAge);

            filledAgeList.add(fullAge);
        }
        buddyDto.setBuddyAgeList(filledAgeList);

        // 4. insertBuddyList 호출 직전 buddyDto.getBuddyAgeList() 다시 확인
        System.out.println("insertBuddyList 호출 직전 buddyDto.buddyAgeList: " + buddyDto.getBuddyAgeList());

        buddyMapper.insertBuddyList(buddyDto);
    }

    public void updateIsBuddy(int userId) {
        buddyMapper.updateIsBuddy(userId);
    }

    public List<Map<String, Object>> getBuddyUserList(int loggedInUserId) {
        return buddyMapper.getBuddyUserList(loggedInUserId);
    }

    // 버디인지확인
    public boolean isBuddy(int userId) {
        // 매퍼를 통해 is_buddy 상태를 조회합니다.
        return buddyMapper.isBuddy(userId);
    }

    // 매칭
    @Transactional
    public void sendMatchingRequest(int sendBuddyId, int receiverBuddyId) {
        buddyMapper.insertMatching(sendBuddyId, receiverBuddyId);
    }

    @Transactional
    public void respondToMatching(int id, String status, int sendBuddyId) {
        buddyMapper.updateMatchingStatus(id, status);

        // if ("수락".equals(status)) {
        // // 수락한 경우만 채팅 시작
        // buddyMapper.insertInitialChat(id, sendBuddyId);
        // }
    }

    public List<Map<String, Object>> getMatchingNotifications(int buddyId) {
        return buddyMapper.selectMatchingNotifications(buddyId);
    }

    // 채팅방리스트
    public List<ChatRoomDto> findChatRoomsByBuddyId(int buddyId) {
        return buddyMapper.getChatRoomsByBuddyId(buddyId);
    }

    // 채팅
    public void insertInitialChat(int matchingId, int sendBuddyId) {
        Map<String, Object> params = new HashMap<>();
        params.put("matchingId", matchingId);
        params.put("sendBuddyId", sendBuddyId);
        buddyMapper.insertInitialChat(params);
    }

     // ✅ 수정: 매칭 ID로 상대방 정보 조회
    public Map<String, Object> getMatchingInfo(int matchingId, int loggedInUserId) {
        // 매퍼를 통해 매칭 정보를 가져옵니다.
        Map<String, Object> matchingInfo = buddyMapper.selectMatchingInfo(matchingId);

        // 💡 Null 체크 추가: matchingInfo가 null일 경우, 더 이상 진행하지 않고 null을 반환합니다.
        if (matchingInfo == null) {
            System.out.println("매칭 ID " + matchingId + "에 대한 정보가 없습니다.");
            return null;
        }

        // 상대방 정보만 추출하여 반환합니다.
        Map<String, Object> result = new HashMap<>();
        
        // 💡 타입 캐스팅 오류 방지
        int buddy1Id = (Integer) matchingInfo.get("buddy1Id");
        int buddy2Id = (Integer) matchingInfo.get("buddy2Id");

        if (buddy1Id == loggedInUserId) {
            result.put("otherBuddyName", matchingInfo.get("buddy2Name"));
            result.put("otherBuddyProfileImage", matchingInfo.get("buddy2ProfileImage"));
        } else if (buddy2Id == loggedInUserId) {
            result.put("otherBuddyName", matchingInfo.get("buddy1Name"));
            result.put("otherBuddyProfileImage", matchingInfo.get("buddy1ProfileImage"));
        }

        return result;
    }

    // public void sendChat(ChatDto chatDto) {
    // if (chatDto.getSentAt() == null) {
    // chatDto.setSentAt(LocalDateTime.now());
    // }
    // chatDto.setRead(false);
    // buddyMapper.insertChat(chatDto);
    // }
    // ✅ sendChat 메서드의 반환 타입을 void에서 ChatDto로 변경합니다.
    public ChatDto sendChat(ChatDto chatDto) {
        if (chatDto.getSentAt() == null) {
            chatDto.setSentAt(LocalDateTime.now());
        }
        chatDto.setRead(false);
        // MyBatis의 <insert> 태그에 `useGeneratedKeys="true" keyProperty="id"`를 추가하면
        // insert 후 chatDto 객체의 id 필드에 DB에서 생성된 ID가 자동으로 채워집니다.
        buddyMapper.insertChat(chatDto);
        return chatDto; // ✅ DB에 저장된 정보(id, sentAt 등)가 채워진 chatDto를 반환합니다.
    }

    public List<ChatDto> getChatsByMatchingId(int matchingId) {
        return buddyMapper.selectChatsByMatchingId(matchingId);
    }

    // 메시지 읽음 처리
    @Transactional
    public void markMessagesAsRead(int matchingId, int readerBuddyId) {
        buddyMapper.markMessagesAsRead(matchingId, readerBuddyId);
    }
}
