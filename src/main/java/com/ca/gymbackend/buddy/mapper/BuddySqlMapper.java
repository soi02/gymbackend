package com.ca.gymbackend.buddy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import com.ca.gymbackend.buddy.dto.BuddyDto;
import com.ca.gymbackend.buddy.dto.ChatDto;
import com.ca.gymbackend.buddy.dto.ChatRoomDto;
// import com.ca.gymbackend.buddy.dto.MatchingDto;
// import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.buddy.dto.AgeDto;

@Mapper
public interface BuddySqlMapper {

    public void insertBuddyList(BuddyDto buddyDto);

    public AgeDto findByAgeId(int id);

    public void updateIsBuddy(int userId);

    public List<Map<String, Object>> getBuddyUserList(int loggedInUserId);

    // 버디인지확인
    public boolean isBuddy(int userId);

    // 매칭 관련 메소드
    public void insertMatching(@Param("sendBuddyId") int sendBuddyId, @Param("receiverBuddyId") int receiverBuddyId);

    public void updateMatchingStatus(@Param("id") int id, @Param("status") String status);

    public List<Map<String, Object>> selectMatchingNotifications(@Param("buddyId") int buddyId);

    public List<ChatRoomDto> getChatRoomsByBuddyId(@Param("buddyId") int buddyId);

    // 채팅
    // ✅ 추가: 매칭 ID로 매칭 정보 조회
    public Map<String, Object> selectMatchingInfo(int matchingId);

    // void insertInitialChat(@Param("matchingId") int matchingId,
    // @Param("sendBuddyId") int sendBuddyId);
    public void insertInitialChat(Map<String, Object> params);

    public void insertChat(ChatDto chatDto);

    public List<ChatDto> selectChatsByMatchingId(int matchingId);

    public void markMessagesAsRead(@Param("matchingId") int matchingId, @Param("readerBuddyId") int readerBuddyId);

}
