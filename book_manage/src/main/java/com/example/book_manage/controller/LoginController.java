package com.example.book_manage.controller;

import com.example.book_manage.dto.UserDto;
import com.example.book_manage.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class LoginController {

    private final List<UserDto> users = Arrays.asList(
            new UserDto("testUser", "1234"),
            new UserDto("admin", "admin1234")
    );

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto loginRequest) {
        System.out.println("loginRequest : " + loginRequest);
        for(UserDto user : users) {
            if(user.getUsername().equals(loginRequest.getUsername()) && user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(Map.of("message", "success", "username", user.getUsername()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid created"));
    }

}
