package com.ca.gymbackend.portal.dto;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String name;
    // private int age;
    private String gender;
    private String accountName;
    private String password;
    private Date birth;
    private String address;
    private String phone;
    private String profileImage;
    private int height;
    private int weight;
    private int muscleMass;
    private boolean isBuddy;
    private LocalDateTime createdAt;
}
