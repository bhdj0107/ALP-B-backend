package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.repository.CodeDetailRepository;
import com.example.demo.repository.CodeGroupRepository;
import com.example.demo.model.CodeGroup;
import com.example.demo.model.CodeDetail;
import com.example.demo.dto.response.CodeGroupResponse;
import com.example.demo.dto.response.CodeDetailResponse;
import com.example.demo.dto.request.CodeGroupCreateRequest;
import com.example.demo.dto.request.CodeDetailCreateRequest;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeService {
    private final CodeGroupRepository codeGroupRepository;
    private final CodeDetailRepository codeDetailRepository;

    // 코드 그룹 목록 조회
    public List<CodeGroupResponse> getAllCodeGroups() {
        return codeGroupRepository.findAll().stream()
                .map(CodeGroupResponse::from)
                .collect(Collectors.toList());
    }

    // 코드 상세 목록 조회
    public List<CodeDetailResponse> getAllCodeDetails() {
        return codeDetailRepository.findAll().stream()
                .map(CodeDetailResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 그룹의 코드 상세 목록 조회
    public List<CodeDetailResponse> getCodeDetailsByGroupId(String groupId) {
        return codeDetailRepository.findByCodeGroup_GroupId(groupId).stream()
                .map(CodeDetailResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CodeGroupResponse createCodeGroup(CodeGroupCreateRequest request) {
        // 중복 체크
        if (codeGroupRepository.existsById(request.getGroup_id())) {
            throw new IllegalArgumentException("이미 존재하는 그룹 ID입니다.");
        }

        CodeGroup codeGroup = CodeGroup.builder()
                .group_id(request.getGroup_id())
                .group_name(request.getGroup_name())
                .description(request.getDescription())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        return CodeGroupResponse.from(codeGroupRepository.save(codeGroup));
    }

    @Transactional
    public CodeDetailResponse createCodeDetail(CodeDetailCreateRequest request) {
        // 그룹 존재 여부 확인
        CodeGroup codeGroup = codeGroupRepository.findById(request.getGroup_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹 ID입니다."));

        // 코드 ID 중복 체크
        if (codeDetailRepository.existsById(request.getCode_id())) {
            throw new IllegalArgumentException("이미 존재하는 코드 ID입니다.");
        }

        CodeDetail codeDetail = CodeDetail.builder()
                .code_id(request.getCode_id())
                .codeGroup(codeGroup)
                .code_name(request.getCode_name())
                .code_value(request.getCode_value())
                .sort_order(request.getSort_order())
                .is_active(request.getIs_active())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        return CodeDetailResponse.from(codeDetailRepository.save(codeDetail));
    }

    @Transactional
    public void deleteCodeGroups(String group_id) {
        // 그룹에 속한 상세 코드들도 함께 삭제
        codeDetailRepository.deleteByCodeGroup_GroupIdIn(group_id);
        codeGroupRepository.deleteById(group_id);
    }

    @Transactional
    public void deleteCodeDetails(List<String> codeIds) {
        codeDetailRepository.deleteAllById(codeIds);
    }
}
