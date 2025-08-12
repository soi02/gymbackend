package com.ca.gymbackend.challenge.dto;

import java.util.List;

import lombok.Data;

@Data
public class KeywordCategoryTree {
    private int keywordCategoryId;
    private String keywordCategoryName;
    private List<KeywordItem> keywords;
}
