package com.ca.gymbackend.routine.request;

import lombok.Data;

@Data
public class MemoUpdateRequest {
    private int elementId;
    private String memoContent;
}
