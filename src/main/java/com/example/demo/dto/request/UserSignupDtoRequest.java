package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupDtoRequest {
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private String phone;
    private String avatar;
}
