package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.request.PasswordResetRequest;
import com.example.demo.dto.request.ResignRequest;
import com.example.demo.dto.request.UserLoginDtoRequest;
import com.example.demo.dto.request.UserSignupDtoRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final String UPLOAD_DIR = "uploads/avatars/";

    @Transactional
    public void signUp(UserSignupDtoRequest userSignupDto) throws NoSuchAlgorithmException {
        logger.info("회원가입 요청 - 입력값: {}", userSignupDto);
        // 해당 유저 이메일이 이미 존재하는지 확인
        String email = userSignupDto.getEmail();

        User user = userRepository.findByEmail(email).orElse(null);

        // 이미 존재하는 이메일이면 예외 발생
        if (user != null) {
            logger.warn("회원가입 실패 - 이미 존재하는 이메일: {}", email);
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        } 
        
        // 해당 유저 정보 저장
        String hashedPassword = PasswordHasher.hashPassword(userSignupDto.getPassword());
        user = User.builder()
            .email(email)
            .password(hashedPassword)
            .name(userSignupDto.getName())
            .phone(userSignupDto.getPhone())
            .signupDate(LocalDateTime.now())
            .avatar(userSignupDto.getAvatar())
            .isAdmin(false)
            .build();
        userRepository.save(user);
        logger.info("회원가입 성공 - 사용자 ID: {}", user.getId());
    }

    @Transactional
    public User login(UserLoginDtoRequest userLoginDto, HttpSession session) throws NoSuchAlgorithmException {
        String sessionId = session.getId();
        logger.info("로그인 요청 - 세션ID: {}, 입력값: {}", sessionId, userLoginDto);
        
        String email = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();
        String hashedPassword = PasswordHasher.hashPassword(password);
        
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            logger.warn("로그인 실패 - 존재하지 않는 이메일: {}, 세션ID: {}", email, sessionId);
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        
        logger.info("로그인 시도 - 사용자 ID: {}, 이메일: {}, 비밀번호: {}", user.getId(), email, password);
        logger.info("로그인 시도 - 사용자 비밀번호: {}, 입력 비밀번호: {}", user.getPassword(), hashedPassword);
        if (user.getPassword().equals(hashedPassword)) {
            session.setAttribute("userID", user.getId());
            logger.info("로그인 성공 - 사용자 ID: {}, 세션ID: {}", user.getId(), session.getId());
        } else {
            logger.warn("로그인 실패 - 잘못된 비밀번호, 이메일: {}, 세션ID: {}", email, sessionId);
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return user;
        
    }

    public void logout(HttpSession session) {
        Long userId = (Long) session.getAttribute("userID");
        String sessionId = session.getId();
        
        logger.info("로그아웃 요청 - 세션ID: {}, 사용자ID: {}", sessionId, userId);
        
        if (userId == null) {
            logger.warn("로그아웃 실패 - 로그인되지 않은 세션: {}", sessionId);
            throw new RuntimeException("로그인 상태가 아닙니다.");
        }
        
        session.invalidate();
        logger.info("로그아웃 성공 - 사용자ID: {}, 세션ID: {}", userId, sessionId);
    }



    @Transactional
    public void changePassword(PasswordResetRequest passwordResetDto, HttpSession session) throws NoSuchAlgorithmException {
        Long userId = (Long) session.getAttribute("userID");
        String sessionId = session.getId();
        logger.info("비밀번호 변경 요청 - 세션ID: {}, 사용자ID: {}", sessionId, userId);
        if (userId == null) {
            logger.warn("비밀번호 변경 실패 - 로그인되지 않은 세션: {}", sessionId);
            throw new RuntimeException("로그인 상태가 아닙니다.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        String hashedCurrentPassword = PasswordHasher.hashPassword(passwordResetDto.getCurrentPassword());
        user.resetPassword(hashedCurrentPassword);
    }

    public User getInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("userID");
        String sessionId = session.getId();
        
        logger.info("사용자 정보 조회 요청 - 세션ID: {}, 사용자ID: {}", sessionId, userId);
        
        if (userId == null) {
            logger.warn("사용자 정보 조회 실패 - 로그인되지 않은 세션: {}", sessionId);
            throw new RuntimeException("로그인 상태가 아닙니다.");
        }
        
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        logger.info("사용자 정보 조회 성공 - 세션ID: {}, 응답: {}", sessionId, user);
        return user;
    }

    @Transactional
    public User modifyInfo(UserSignupDtoRequest userSignupDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userID");
        String sessionId = session.getId();
        
        logger.info("사용자 정보 수정 요청 - 세션ID: {}, 사용자ID: {}, 입력값: {}", sessionId, userId, userSignupDto.toString());
        
        if (userId == null) {
            logger.warn("사용자 정보 수정 실패 - 로그인되지 않은 세션: {}", sessionId);
            throw new RuntimeException("로그인 상태가 아닙니다.");
        }
        
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.modifyInfo(userSignupDto);

        logger.info("사용자 정보 수정 성공 - 세션ID: {}, 응답: {}", sessionId, user);
        return user;
    }

    @Transactional
    public void resign(ResignRequest resignRequest, HttpSession session) throws NoSuchAlgorithmException {        
        Long userId = (Long) session.getAttribute("userID");
        String sessionId = session.getId();
        
        logger.info("회원탈퇴 요청 - 세션ID: {}, 사용자ID: {}", sessionId, userId);
        
        if (userId == null) {
            logger.warn("회원탈퇴 실패 - 로그인되지 않은 세션: {}", sessionId);
            throw new RuntimeException("로그인 상태가 아닙니다.");
        }

        // 비밀번호 검증
        String password = resignRequest.getPassword();
        if (password == null || password.trim().isEmpty()) {
            logger.warn("회원탈퇴 실패 - 비밀번호가 제공되지 않음");
            throw new RuntimeException("비밀번호를 입력해주세요.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        String hashedPassword = PasswordHasher.hashPassword(password);
        System.out.println(user.getPassword());
        System.out.println(hashedPassword);
        System.out.println("--------------------------------");
        if (!user.getPassword().equals(hashedPassword)) {
            logger.warn("회원탈퇴 실패 - 비밀번호 불일치, 사용자ID: {}", userId);
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 1. 회원 정보 삭제
        userRepository.deleteById(userId);
        logger.info("회원정보 삭제 완료 - 사용자ID: {}", userId);

        // 2. 세션 초기화
        session.invalidate();
        logger.info("세션 초기화 완료 - 세션ID: {}", sessionId);

        return;

    }

    @Transactional
    public String updateAvatar(MultipartFile file, HttpSession session) {
        Long userId = (Long) session.getAttribute("userID");
        String sessionId = session.getId();
        
        logger.info("프로필 이미지 업로드 요청 - 세션ID: {}, 사용자ID: {}, 파일정보: [이름: {}, 크기: {}, 타입: {}]", 
            sessionId, userId, file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        if (userId == null) {
            logger.warn("프로필 이미지 업로드 실패 - 로그인되지 않은 세션: {}", sessionId);
            throw new RuntimeException("로그인 상태가 아닙니다.");
        }

        try {
            // 파일 용량 체크 (3MB)
            if (file.getSize() > 3 * 1024 * 1024) {
                logger.warn("프로필 이미지 업로드 실패 - 파일 용량 초과: {} bytes", file.getSize());
                throw new RuntimeException("파일 용량은 3MB 이하이어야 합니다.");
            }

            // 원본 파일명이 null인 경우 처리
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                logger.warn("프로필 이미지 업로드 실패 - 파일명이 없음");
                throw new RuntimeException("유효하지 않은 파일입니다.");
            }

            // 이미지 유효성 검사
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                logger.warn("프로필 이미지 업로드 실패 - 유효하지 않은 이미지 파일: {}", originalFilename);
                throw new RuntimeException("유효하지 않은 이미지 파일입니다.");
            }

            // 이미지 리사이즈 (128x128)
            BufferedImage resizedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(bufferedImage.getScaledInstance(128, 128, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();

            // 절대 경로로 업로드 디렉토리 생성
            String uploadPath = new File("").getAbsolutePath() + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                logger.info("업로드 디렉토리 생성: {}", uploadPath);
                if (!uploadDir.mkdirs()) {
                    logger.error("디렉토리 생성 실패: {}", uploadPath);
                    throw new RuntimeException("서버 설정 오류가 발생했습니다.");
                }
            }

            // 사용자 아바타 URL 업데이트
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            String existingAvatarUrl = user.getAvatar();
            if (existingAvatarUrl != null) {
                // 기존 아바타 파일 삭제
                String existingFileName = existingAvatarUrl.substring(existingAvatarUrl.lastIndexOf("/") + 1);
                Path existingFilePath = Paths.get(uploadPath, existingFileName);
                Files.deleteIfExists(existingFilePath);
                logger.info("기존 아바타 파일 삭제: {}", existingFilePath);
            }

            // 파일 확장자 추출 및 검증
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".svg").contains(fileExtension)) {
                logger.warn("프로필 이미지 업로드 실패 - 지원하지 않는 파일 형식: {}", fileExtension);
                throw new RuntimeException("지원하지 않는 이미지 형식입니다.");
            }

            // 파일명 생성 및 저장
            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = Paths.get(uploadPath, fileName);
            
            logger.info("파일 저장 경로: {}", filePath);

            // 리사이즈된 이미지 저장
            ImageIO.write(resizedImage, fileExtension.substring(1), filePath.toFile());

            // 사용자 아바타 URL 업데이트
            String avatarUrl = "/api/v1/user/avatar/" + fileName;
            User updatedUser = User.builder()
                .id(userId)
                .avatar(avatarUrl)
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .signupDate(user.getSignupDate())
                .build();
            userRepository.save(updatedUser);

            logger.info("프로필 이미지 업로드 성공 - 사용자ID: {}, 파일명: {}, URL: {}", 
                userId, fileName, avatarUrl);
            return avatarUrl;
        } catch (IOException e) {
            logger.error("프로필 이미지 업로드 실패 - 사용자ID: {}, 에러: {}", userId, e.getMessage(), e);
            throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }
}

class PasswordHasher {
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        String algorithm = "SHA-256";
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] digestedPassword = md.digest(password.getBytes());

        // transform byte array to string  
        StringBuilder sb = new StringBuilder();
        for (byte b : digestedPassword) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}