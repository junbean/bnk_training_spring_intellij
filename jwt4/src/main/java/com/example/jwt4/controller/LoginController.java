package com.example.jwt4.controller;

import com.example.jwt4.dto.LoginRequest;
import com.example.jwt4.utils.JWTUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final JWTUtil jwtUtil;

    public LoginController(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        if("test".equals(loginRequest.getUsername()) && "1234".equals(loginRequest.getPassword())) {
            // 10분 동안 유효한 토큰
            String token = jwtUtil.createJwt(loginRequest.getUsername(), 1000 * 60 * 10);
            return ResponseEntity.ok().body("Bearer " + token);
        } else {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 틀렸습니다");
        }
    }
}
