package com.ca.gymbackend.buddy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import com.ca.gymbackend.buddy.dto.BuddyDto;
import com.ca.gymbackend.buddy.dto.ChatDto;
import com.ca.gymbackend.buddy.dto.ChatRoomDto;
import com.ca.gymbackend.buddy.dto.AgeDto;

@Mapper
public interface BuddySqlMapper {

    public void insertBuddyList(BuddyDto buddyDto);

    public AgeDto findByAgeId(int id);

    public void updateIsBuddy(int userId);

    public List<Map<String, Object>> getBuddyUserList(int loggedInUserId);

    public boolean isBuddy(int userId);

    public void insertMatching(@Param("sendBuddyId") int sendBuddyId, @Param("receiverBuddyId") int receiverBuddyId);

    public void updateMatchingStatus(@Param("id") int id, @Param("status") String status);

    public List<Map<String, Object>> selectMatchingNotifications(@Param("buddyId") int buddyId);

    public List<ChatRoomDto> getChatRoomsByBuddyId(@Param("buddyId") int buddyId);

    public Map<String, Object> selectMatchingInfo(int matchingId);

    public void insertInitialChat(Map<String, Object> params);

    public void insertChat(ChatDto chatDto);

    public List<ChatDto> selectChatsByMatchingId(int matchingId);
    
    // ⭐ 추가: 읽지 않은 메시지 ID 목록을 조회하는 메서드
    public List<Integer> selectUnreadChatIds(@Param("matchingId") int matchingId, @Param("readerBuddyId") int readerBuddyId);

    // ⭐ 수정: 매개변수에 @Param 어노테이션 추가
    public void markMessagesAsRead(@Param("matchingId") int matchingId, @Param("readerBuddyId") int readerBuddyId);

}