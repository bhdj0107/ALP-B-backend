package com.example.demo.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentPetsitterResponse {
    private Long id;
    private String introduction;
    private String experience;
    private String availableTime;
    private String location;
    private int price;
    private int totalServices;
    private int totalReviews;
    private float rating;
    private Boolean isApproved;
    private List<String> services;
    private List<String> codeGroupIds;
    private List<CodeGroupInfo> codeGroups;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CodeGroupInfo {
        private String group_id;
        private String group_name;
        private String description;
    }
}
