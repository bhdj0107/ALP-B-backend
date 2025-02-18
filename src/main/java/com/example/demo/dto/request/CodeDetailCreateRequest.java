package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeDetailCreateRequest {
    private String code_id;
    private String group_id;
    private String code_name;
    private String code_value;
    private Integer sort_order;
    private Boolean is_active;
} 