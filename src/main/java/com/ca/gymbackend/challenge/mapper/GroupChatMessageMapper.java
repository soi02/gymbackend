package com.ca.gymbackend.challenge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.challenge.dto.groupchat.GroupChatMessage;

@Mapper
public interface GroupChatMessageMapper {
    
    // 새로운 그룹 채팅 메시지를 데이터베이스에 삽입
    public void insertMessage(GroupChatMessage groupChatMessage);

    // 특정 챌린지의 모든 메시지 기록을 조회
    public List<GroupChatMessage> findAllMessagesByChallengeId(@Param("challengeId") Long challengeId);
}
