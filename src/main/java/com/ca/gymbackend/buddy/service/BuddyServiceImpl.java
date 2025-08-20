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
        System.out.println("초기 buddyDto.buddyAgeList: " + buddyDto.getBuddyAgeList());

        List<AgeDto> filledAgeList = new ArrayList<>();
        for (AgeDto age : buddyDto.getBuddyAgeList()) {
            System.out.println("findByAgeId 호출 전 age.getId(): " + age.getId());
            AgeDto fullAge = buddyMapper.findByAgeId(age.getId());
            System.out.println("findByAgeId 호출 후 fullAge: " + fullAge);
            filledAgeList.add(fullAge);
        }
        buddyDto.setBuddyAgeList(filledAgeList);
        System.out.println("insertBuddyList 호출 직전 buddyDto.buddyAgeList: " + buddyDto.getBuddyAgeList());
        buddyMapper.insertBuddyList(buddyDto);
    }

    public void updateIsBuddy(int userId) {
        buddyMapper.updateIsBuddy(userId);
    }

    public List<Map<String, Object>> getBuddyUserList(int loggedInUserId) {
        return buddyMapper.getBuddyUserList(loggedInUserId);
    }

    public boolean isBuddy(int userId) {
        return buddyMapper.isBuddy(userId);
    }

    @Transactional
    public void sendMatchingRequest(int sendBuddyId, int receiverBuddyId) {
        buddyMapper.insertMatching(sendBuddyId, receiverBuddyId);
    }

    @Transactional
    public void respondToMatching(int id, String status, int sendBuddyId) {
        buddyMapper.updateMatchingStatus(id, status);
    }

    public List<Map<String, Object>> getMatchingNotifications(int buddyId) {
        return buddyMapper.selectMatchingNotifications(buddyId);
    }

    public List<ChatRoomDto> findChatRoomsByBuddyId(int buddyId) {
        return buddyMapper.getChatRoomsByBuddyId(buddyId);
    }

    public void insertInitialChat(int matchingId, int sendBuddyId) {
        Map<String, Object> params = new HashMap<>();
        params.put("matchingId", matchingId);
        params.put("sendBuddyId", sendBuddyId);
        buddyMapper.insertInitialChat(params);
    }

    public Map<String, Object> getMatchingInfo(int matchingId, int loggedInUserId) {
        Map<String, Object> matchingInfo = buddyMapper.selectMatchingInfo(matchingId);
        if (matchingInfo == null) {
            System.out.println("매칭 ID " + matchingId + "에 대한 정보가 없습니다.");
            return null;
        }

        Map<String, Object> result = new HashMap<>();
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

    // ✅ 수정: sendChat 메서드
    @Transactional
    public ChatDto sendChat(ChatDto chatDto) {
        if (chatDto.getSentAt() == null) {
            chatDto.setSentAt(LocalDateTime.now());
        }
        chatDto.setRead(false);
        buddyMapper.insertChat(chatDto);
        return chatDto;
    }

    public List<ChatDto> getChatsByMatchingId(int matchingId) {
        return buddyMapper.selectChatsByMatchingId(matchingId);
    }

    // ✅ 수정: markMessagesAsRead 메서드
    @Transactional
    public List<Integer> markMessagesAsRead(int matchingId, int readerBuddyId) {
        List<Integer> chatIdsToRead = buddyMapper.selectUnreadChatIds(matchingId, readerBuddyId);
        if (!chatIdsToRead.isEmpty()) {
            buddyMapper.markMessagesAsRead(matchingId, readerBuddyId);
        }
        return chatIdsToRead;
    }
}