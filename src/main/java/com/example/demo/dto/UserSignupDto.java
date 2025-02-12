package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupDto {
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private String phone;
}
