package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeGroupCreateRequest {
    private String group_id;
    private String group_name;
    private String description;
} 