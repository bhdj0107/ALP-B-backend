package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.service.CodeService;
import com.example.demo.dto.response.CodeGroupResponse;
import com.example.demo.dto.response.CodeDetailResponse;
import com.example.demo.dto.request.CodeGroupCreateRequest;
import com.example.demo.dto.request.CodeDetailCreateRequest;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/v1/codes")
@RequiredArgsConstructor
public class CodeController {
    private final CodeService codeService;

    @GetMapping("/groups")
    public List<CodeGroupResponse> getAllCodeGroups() {
        return codeService.getAllCodeGroups();
    }

    @GetMapping("/details")
    public List<CodeDetailResponse> getAllCodeDetails() {
        return codeService.getAllCodeDetails();
    }

    @GetMapping("/groups/{groupId}/details")
    public List<CodeDetailResponse> getCodeDetailsByGroupId(
        @PathVariable("groupId") String groupId
    ) {
        return codeService.getCodeDetailsByGroupId(groupId);
    }

    @PostMapping("/groups")
    public CodeGroupResponse createCodeGroup(@RequestBody CodeGroupCreateRequest request) {
        return codeService.createCodeGroup(request);
    }

    @PostMapping("/details")
    public CodeDetailResponse createCodeDetail(@RequestBody CodeDetailCreateRequest request) {
        return codeService.createCodeDetail(request);
    }

    @DeleteMapping("/groups")
    public void deleteCodeGroups(@RequestBody String group_id) {
        codeService.deleteCodeGroups(group_id);
    }

    @DeleteMapping("/details")
    public void deleteCodeDetails(@RequestBody List<String> codeIds) {
        codeService.deleteCodeDetails(codeIds);
    }
}
