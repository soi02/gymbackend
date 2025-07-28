package com.ca.gymbackend.buddy.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ca.gymbackend.buddy.dto.BuddyDto;

@Mapper
public interface BuddySqlMapper {

    public void insertBuddyList(BuddyDto buddyDto);
}
