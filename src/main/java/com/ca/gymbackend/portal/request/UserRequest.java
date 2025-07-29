package com.ca.gymbackend.portal.request;

import java.sql.Date;

import lombok.Data;

@Data
public class UserRequest {
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
}
