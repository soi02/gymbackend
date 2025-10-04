package com.ca.gymbackend.challenge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
// import org.apache.ibatis.annotations.Select;

import com.ca.gymbackend.challenge.dto.groupchat.GroupChatListItemDto;
import com.ca.gymbackend.challenge.dto.groupchat.GroupChatMessage;

@Mapper
public interface GroupChatMessageMapper {
    
    // 새로운 그룹 채팅 메시지를 데이터베이스에 삽입
    public void insertMessage(GroupChatMessage groupChatMessage);

    // 특정 챌린지의 모든 메시지 기록을 조회
    public List<GroupChatMessage> findAllMessagesByChallengeId(@Param("challengeId") Long challengeId);


    // 메시지 읽음 상태 저장
    public void insertMessageReadStatus(@Param("messageId") Long messageId, @Param("userId") Long userId);
    
    // 특정 메시지를 이미 읽었는지 확인
    public boolean hasReadStatus(@Param("messageId") Long messageId, @Param("userId") Long userId);
    
    // 특정 메시지를 읽은 사용자 수 조회
    public Long countReadersByMessageId(@Param("messageId") Long messageId);
    
    // 메시지 ID로 챌린지 ID 조회
    public Long findChallengeIdByMessageId(@Param("messageId") Long messageId);




    // 챌린지 참여 인원 수 조회 메서드 추가
    public int countParticipantsByChallengeId(@Param("challengeId") Long challengeId);



    // 챌린지 ID로 챌린지 제목을 조회하는 SQL 쿼리 메서드 추가
    // @Select("SELECT challenge_name FROM challenge WHERE challenge_id = #{challengeId}")
    public String findChallengeTitleById(@Param("challengeId") Long challengeId);





    // 채팅 목록 + 요약
    public List<GroupChatListItemDto> selectJoinedChallengesWithChatSummary(@Param("userId") Long userId);
}
