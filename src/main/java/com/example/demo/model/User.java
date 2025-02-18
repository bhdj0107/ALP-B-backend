package com.example.demo.model;

import java.time.LocalDateTime;

import com.example.demo.dto.request.UserSignupDtoRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", indexes = {@Index(name = "email_index", columnList = "email", unique = true)})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private LocalDateTime signupDate;
    private LocalDateTime lastModifiedDate;
    private String avatar;
    private boolean isAdmin;

    public void resetPassword(String newPassword) {
        this.password = newPassword;
    }
    
    public void modifyInfo(UserSignupDtoRequest userSignupDto) {
        this.name = userSignupDto.getName();
        this.email = userSignupDto.getEmail();
        this.phone = userSignupDto.getPhone();
        this.avatar = userSignupDto.getAvatar();
        this.lastModifiedDate = LocalDateTime.now();
    }
}

