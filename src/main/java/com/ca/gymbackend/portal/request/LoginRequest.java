package com.ca.gymbackend.portal.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String accountName;
    private String password;
}
