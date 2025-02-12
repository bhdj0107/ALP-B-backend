package com.example.demo.contoller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserSignupDto;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ObjectMapper mapper;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupDto userSignupDto) throws NoSuchAlgorithmException{
        String email = userSignupDto.getEmail();
        String password = userSignupDto.getPassword();
        
        // password byte-hashing
        String hashedPassword = PasswordHasher.hashPassword(password);

        // check if user already exists
        User user = userService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.status(401).body("User already exists");
        } 
        
        // else, create new user
        else {
            user = new User();
            user.setEmail(email);
            user.setPassword(hashedPassword);
            user.setName(userSignupDto.getName());
            user.setPhone(userSignupDto.getPhone());
            user.setSignupDate(LocalDateTime.now());
            user = userService.save(user);
        }

        return ResponseEntity.ok("User created");
        
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) throws NoSuchAlgorithmException {
        String email = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();
        
        // password byte-hashing
        String hashedPassword = PasswordHasher.hashPassword(password);
        
        // check if user exists 
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 일치하지 않습니다.");
        } else {

            System.out.println(user.getPassword());
            System.out.println(hashedPassword);
            System.out.println("-----------------");
            if (user.getPassword().equals(hashedPassword)) {
                HttpSession session = request.getSession();
                session.setAttribute("userID", user.getId());
                return ResponseEntity.ok("로그인 성공");
                
            } else {
                return ResponseEntity.status(401).body("아이디 또는 비밀번호가 일치하지 않습니다.");
            }
        }
    }

    @GetMapping("logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userID");
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
        } else {
            session.invalidate();
            return ResponseEntity.ok("로그아웃 성공");
        }
    }

    @GetMapping("session")
    public ResponseEntity<String> currentSession(HttpServletRequest request) throws JsonProcessingException{
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userID");
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
        } else {
            User user = userService.findById(userId);
            return ResponseEntity.ok(mapper.writeValueAsString(user));
        }
    }
    
    @PostMapping("reset")
    public ResponseEntity<String> reset(@RequestBody String newPassword, HttpServletRequest request) throws NoSuchAlgorithmException {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userID");
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
        } else {
            User user = userService.findById(userId);
            user.setPassword(PasswordHasher.hashPassword(newPassword));
            userService.update(user);
            session.invalidate();
            return ResponseEntity.ok("비밀번호 변경 성공");
        }
    }

    @GetMapping("info")
    public ResponseEntity<String> getInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userID");    
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
        } else {
            User user = userService.findById(userId);

            String email = user.getEmail();
            String name = user.getName();
            String phone = user.getPhone();
            LocalDateTime signupDate = user.getSignupDate();

            return ResponseEntity.ok("email: " + email + "\nname: " + name + "\nphone: " + phone + "\nsignupDate: " + signupDate);
        }
    }

    @PostMapping("info")
    public ResponseEntity<String> info(@RequestBody UserSignupDto userSignupDto, HttpServletRequest request) throws NoSuchAlgorithmException {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userID");
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
        } else {
            User user = userService.findById(userId);
            user.setEmail(userSignupDto.getEmail());
            user.setName(userSignupDto.getName());
            user.setPhone(userSignupDto.getPhone());
            userService.update(user);
            return ResponseEntity.ok(user.toString());
        }
    }
    
    @GetMapping("resign")
    public ResponseEntity<String> resign(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userID");
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
        } else {
            userService.deleteById(userId);
            session.invalidate();
            return ResponseEntity.ok("회원탈퇴 성공");
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