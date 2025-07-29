package com.ca.gymbackend.portal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private String name;
    private Integer id;

    // 메시지 응답용 생성자
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 이름 + id 응답용 생성자
    public ApiResponse(boolean success, String name, Integer id) {
        this.success = success;
        this.name = name;
        this.id = id;
    }
}
