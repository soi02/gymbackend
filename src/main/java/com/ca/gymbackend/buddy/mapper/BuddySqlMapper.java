package com.ca.gymbackend.buddy.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

import com.ca.gymbackend.buddy.dto.BuddyDto;
// import com.ca.gymbackend.portal.dto.UserDto;
import com.ca.gymbackend.buddy.dto.AgeDto;

@Mapper
public interface BuddySqlMapper {

    public void insertBuddyList(BuddyDto buddyDto);

    public AgeDto findByAgeId(int id);

    public void updateIsBuddy(int userId);

    public List<Map<String, Object>> getBuddyUserList();

}
