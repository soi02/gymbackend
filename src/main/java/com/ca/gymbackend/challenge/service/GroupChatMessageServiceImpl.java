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


    // 👇 메시지 읽음 상태 저장 (중복 방지 로직 추가)
    public void saveMessageReadStatus(Long messageId, Long userId) {
        // 해당 메시지를 이미 읽은 기록이 있는지 확인합니다.
        boolean alreadyRead = groupChatMessageMapper.hasReadStatus(messageId, userId);
        if (!alreadyRead) {
            groupChatMessageMapper.insertMessageReadStatus(messageId, userId);
        }
    }
    
    // 👇 특정 메시지의 읽은 사람 수 조회
    public Long getReadCountByMessageId(Long messageId) {
        return groupChatMessageMapper.countReadersByMessageId(messageId);
    }

    // 👇 메시지 ID로 챌린지 ID 조회
    public Long findChallengeIdByMessageId(Long messageId) {
        return groupChatMessageMapper.findChallengeIdByMessageId(messageId);
    }




        // 👇 챌린지 참여 인원 수 조회
    public int getParticipantCountByChallengeId(Long challengeId) {
        return groupChatMessageMapper.countParticipantsByChallengeId(challengeId);
    }
}
