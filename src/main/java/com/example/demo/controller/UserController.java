package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.PasswordResetRequest;
import com.example.demo.dto.request.ResignRequest;
import com.example.demo.dto.request.UserLoginDtoRequest;
import com.example.demo.dto.request.UserSignupDtoRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.NoSuchAlgorithmException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final String UPLOAD_DIR = "uploads/avatars/";

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupDtoRequest userSignupDto) throws NoSuchAlgorithmException {
        try {
            userService.signUp(userSignupDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
        return ResponseEntity.ok("회원가입 완료");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDtoRequest userLoginDto, HttpSession session) throws NoSuchAlgorithmException, JsonProcessingException {
        try {
            User user = userService.login(userLoginDto, session);
            return ResponseEntity.ok(mapper.writeValueAsString(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("logout")
    public ResponseEntity<String> logout(HttpSession session) {
        try {
            userService.logout(session);
            return ResponseEntity.ok("로그아웃 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("session")
    public ResponseEntity<String> currentSession(HttpSession session) throws JsonProcessingException {
        Long userId = (Long) session.getAttribute("userID");
        String sessionId = session.getId();
        
        logger.info("세션 확인 요청 - 세션ID: {}, 사용자ID: {}", sessionId, userId);
        
        if (userId == null) {
            logger.warn("세션 확인 실패 - 로그인되지 않은 세션: {}", sessionId);
            return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
        }
        
        String responseBody = mapper.writeValueAsString(userId);
        logger.info("세션 확인 성공 - 세션ID: {}, 응답: {}", sessionId, responseBody);
        return ResponseEntity.ok(mapper.writeValueAsString(userService.getInfo(session)));
    }
    
    @PostMapping("reset")
    public ResponseEntity<String> reset(@RequestBody PasswordResetRequest passwordResetDto, HttpSession session) throws NoSuchAlgorithmException {
        try {
            userService.changePassword(passwordResetDto, session);
            return ResponseEntity.ok("비밀번호 변경 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("info")
    public ResponseEntity<String> getInfo(HttpSession session) throws JsonProcessingException {
        try {
            User user = userService.getInfo(session);
            return ResponseEntity.ok(mapper.writeValueAsString(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("info")
    public ResponseEntity<String> info(@RequestBody UserSignupDtoRequest userSignupDto, HttpSession session) throws NoSuchAlgorithmException, JsonProcessingException {
        try {
            User user = userService.modifyInfo(userSignupDto, session);
            return ResponseEntity.ok(mapper.writeValueAsString(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    
    @PostMapping("resign")
    public ResponseEntity<String> resign(@RequestBody ResignRequest resignRequest, HttpSession session) throws NoSuchAlgorithmException {
        try {
            userService.resign(resignRequest, session);
            return ResponseEntity.ok("회원탈퇴 완료");
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        try {
            String avatarUrl = userService.updateAvatar(file, session);
            return ResponseEntity.ok(avatarUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("avatar/{fileName}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable("fileName") String fileName) throws IOException {
        // 절대 경로 사용
        String uploadPath = new File("").getAbsolutePath() + File.separator + UPLOAD_DIR;
        Path filePath = Paths.get(uploadPath, fileName);
        logger.info("이미지 파일 요청: {}", filePath);
        
        if (!Files.exists(filePath)) {
            logger.warn("이미지 파일 없음: {}", filePath);
            return ResponseEntity.notFound().build();
        }
        
        byte[] image = Files.readAllBytes(filePath);
        logger.info("이미지 파일 로드 완료 - 크기: {} bytes", image.length);
        
        // 파일 확장자에 따른 Content-Type 설정
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        MediaType mediaType;
        
        switch (fileExtension) {
            case ".svg":
                mediaType = MediaType.valueOf("image/svg+xml");
                break;
            case ".png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case ".gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case ".jpg":
            case ".jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(image);
    }
}

