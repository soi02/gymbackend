package com.ca.gymbackend.buddy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.buddy.dto.BuddyDto;
import com.ca.gymbackend.buddy.service.BuddyServiceImpl;

@RestController
@RequestMapping("/api/buddy")
public class BuddyController {

    @Autowired
    private BuddyServiceImpl buddyService;

    @PostMapping("/register")
    public String registerBuddy(@RequestBody BuddyDto buddyDto) {
        buddyService.registerBuddy(buddyDto);
        System.out.println("넘어온 buddyDto: " + buddyDto);
        System.out.println("buddyAgeList: " + buddyDto.getBuddyAgeList());
        return "버디 등록 완료";
    }
}
