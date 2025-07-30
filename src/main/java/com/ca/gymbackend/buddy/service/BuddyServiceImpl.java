package com.ca.gymbackend.buddy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ca.gymbackend.buddy.dto.AgeDto;
import com.ca.gymbackend.buddy.dto.BuddyDto;
import com.ca.gymbackend.buddy.mapper.BuddySqlMapper;

@Service
public class BuddyServiceImpl {
    @Autowired
    private BuddySqlMapper buddyMapper;

    // BuddyServiceImpl.java 의 registerBuddy 메소드
    public void registerBuddy(BuddyDto buddyDto) {
        // 1. 처음 넘어온 buddyDto.getBuddyAgeList() 확인
        System.out.println("초기 buddyDto.buddyAgeList: " + buddyDto.getBuddyAgeList());

        List<AgeDto> filledAgeList = new ArrayList<>();
        for (AgeDto age : buddyDto.getBuddyAgeList()) {
            // 2. findByAgeId 호출 전 age.getId() 값 확인
            System.out.println("findByAgeId 호출 전 age.getId(): " + age.getId());

            AgeDto fullAge = buddyMapper.findByAgeId(age.getId());

            // 3. findByAgeId 호출 후 fullAge 객체의 id, age 값 확인
            System.out.println("findByAgeId 호출 후 fullAge: " + fullAge);

            filledAgeList.add(fullAge);
        }
        buddyDto.setBuddyAgeList(filledAgeList);

        // 4. insertBuddyList 호출 직전 buddyDto.getBuddyAgeList() 다시 확인
        System.out.println("insertBuddyList 호출 직전 buddyDto.buddyAgeList: " + buddyDto.getBuddyAgeList());

        buddyMapper.insertBuddyList(buddyDto);
    }

    public void updateIsBuddy(int userId) {
        buddyMapper.updateIsBuddy(userId);
    }

    public List<Map<String, Object>> getBuddyUserList() {
        return buddyMapper.getBuddyUserList();
    }

    // 매칭
    @Transactional
    public void sendMatchingRequest(int sendBuddyId, int receiverBuddyId) {
        buddyMapper.insertMatching(sendBuddyId, receiverBuddyId);
    }

    @Transactional
    public void respondToMatching(int id, String status, int sendBuddyId) {
        buddyMapper.updateMatchingStatus(id, status);

        // if ("수락".equals(status)) {
        //     // 수락한 경우만 채팅 시작
        //     buddyMapper.insertInitialChat(id, sendBuddyId);
        // }
    }

    public List<Map<String, Object>> getMatchingNotifications(int buddyId) {
        return buddyMapper.selectMatchingNotifications(buddyId);
    }
}
