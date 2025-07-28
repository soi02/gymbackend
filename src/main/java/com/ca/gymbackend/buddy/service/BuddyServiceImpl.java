package com.ca.gymbackend.buddy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.gymbackend.buddy.dto.BuddyDto;
import com.ca.gymbackend.buddy.mapper.BuddySqlMapper;

@Service
public class BuddyServiceImpl {
    @Autowired
    private BuddySqlMapper buddyMapper;
    public void registerBuddy(BuddyDto buddyDto) {
        buddyMapper.insertBuddyList(buddyDto);
    }
}
