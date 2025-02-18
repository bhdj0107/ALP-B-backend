package com.example.demo.dto.response;

import com.example.demo.model.CodeDetail;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeDetailResponse {
    private String code_id;
    private String group_id;
    private String code_name;
    private String code_value;
    private int sort_order;
    private boolean is_active;

    public static CodeDetailResponse from(CodeDetail entity) {
        return CodeDetailResponse.builder()
                .code_id(entity.getCode_id())
                .group_id(entity.getCodeGroup().getGroup_id())
                .code_name(entity.getCode_name())
                .code_value(entity.getCode_value())
                .sort_order(entity.getSort_order())
                .is_active(entity.is_active())
                .build();
    }
} 