package com.example.spring_jwt.service;

import com.example.spring_jwt.dto.LoginResponseDTO;
import com.example.spring_jwt.dto.UserSignupDTO;
import com.example.spring_jwt.entity.User;
import com.example.spring_jwt.repository.UserRepository;
import com.example.spring_jwt.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    public String register(UserSignupDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            return "이미 존재하는 사용자입니다.";
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .role("USER")
                .build();

        userRepository.save(user);
        return "회원가입 성공";
    }

    public LoginResponseDTO login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return new LoginResponseDTO(accessToken, refreshToken);
    }
}
