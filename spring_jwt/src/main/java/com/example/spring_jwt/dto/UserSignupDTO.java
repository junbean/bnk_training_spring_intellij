package com.example.spring_jwt.dto;

import lombok.Data;

@Data
public class UserSignupDTO {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
}
