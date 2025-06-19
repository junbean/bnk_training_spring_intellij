package com.example.spring_jwt.service;

import com.example.spring_jwt.dto.UserSignupDTO;
import com.example.spring_jwt.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public String register(UserSignupDTO dto) {
        if(userRepository.existsByUsername(dto.getUsername())) {
            return "이미 존재하는 사용자입니다";
        }
    }




}
