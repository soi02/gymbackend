package com.ca.gymbackend.challenge.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.challenge.dto.groupchat.GroupChatMessage;
import com.ca.gymbackend.challenge.mapper.GroupChatMessageMapper;

@Service
public class GroupChatMessageServiceImpl {
    
    @Autowired
    private GroupChatMessageMapper groupChatMessageMapper;

    // 새로운 그룹 채팅 메시지를 데이터베이스에 저장
    public GroupChatMessage saveMessage(GroupChatMessage groupChatMessage) {
        groupChatMessage.setCreatedAt(LocalDateTime.now());
        groupChatMessageMapper.insertMessage(groupChatMessage);
        return groupChatMessage;
    }

    // 특정 챌린지의 모든 메시지 기록을 조회
    public List<GroupChatMessage> getAllMessagesByChallengeId(Long challengeId) {
        return groupChatMessageMapper.findAllMessagesByChallengeId(challengeId);
    }
}
