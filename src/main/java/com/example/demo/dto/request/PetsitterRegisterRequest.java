package com.example.demo.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetsitterRegisterRequest {
    private String introduction;
    private String experience;
    private String location;
    private Integer price;
    private String availableTime;
    private List<String> certifications;
    private List<String> services;
    private List<String> codeGroupIds;
}
