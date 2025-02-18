package com.example.demo.dto.response;

import com.example.demo.model.CodeGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CodeGroupResponse {
    private String group_id;
    private String group_name;
    private String description;

    public static CodeGroupResponse from(CodeGroup entity) {
        return CodeGroupResponse.builder()
                .group_id(entity.getGroup_id())
                .group_name(entity.getGroup_name())
                .description(entity.getDescription())
                .build();
    }
} 