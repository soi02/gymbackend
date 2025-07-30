package com.ca.gymbackend.buddy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import com.ca.gymbackend.buddy.dto.BuddyDto;
// import com.ca.gymbackend.buddy.dto.MatchingDto;
// import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.buddy.dto.AgeDto;

@Mapper
public interface BuddySqlMapper {

    public void insertBuddyList(BuddyDto buddyDto);

    public AgeDto findByAgeId(int id);

    public void updateIsBuddy(int userId);

    public List<Map<String, Object>> getBuddyUserList();

    public void insertMatching(@Param("sendBuddyId") int sendBuddyId, @Param("receiverBuddyId") int receiverBuddyId);

    public void updateMatchingStatus(@Param("id") int id, @Param("status") String status);
    // void insertInitialChat(@Param("matchingId") int matchingId, @Param("sendBuddyId") int sendBuddyId);

}
