package com.example.demo.mapper;

import com.example.demo.dto.response.CurrentPetsitterResponse;
import com.example.demo.model.Petsitter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class PetsitterMapper {
    public CurrentPetsitterResponse petsitterToCurrentPetsitterResponse(Petsitter petsitter) {
        List<CurrentPetsitterResponse.CodeGroupInfo> codeGroups = 
            petsitter.getPetsitterCodes() != null ? 
            petsitter.getPetsitterCodes().stream()
                .map(pc -> CurrentPetsitterResponse.CodeGroupInfo.builder()
                    .group_id(pc.getCodeGroup().getGroup_id())
                    .group_name(pc.getCodeGroup().getGroup_name())
                    .description(pc.getCodeGroup().getDescription())
                    .build())
                .collect(Collectors.toList())
            : new ArrayList<>();

        List<String> codeGroupIds = 
            petsitter.getPetsitterCodes() != null ?
            petsitter.getPetsitterCodes().stream()
                .map(pc -> pc.getCodeGroup().getGroup_id())
                .collect(Collectors.toList())
            : new ArrayList<>();

        return CurrentPetsitterResponse.builder()
                .id(petsitter.getId())
                .introduction(petsitter.getIntroduction())
                .location(petsitter.getLocation())
                .price(petsitter.getPrice())
                .availableTime(petsitter.getAvailableTime())
                .isApproved(petsitter.getIsApproved())
                .experience(petsitter.getExperience())
                .totalServices(0)
                .totalReviews(0)
                .rating(0)
                .services(petsitter.getServices())
                .codeGroupIds(codeGroupIds)
                .codeGroups(codeGroups)
                .build();
    }
}
