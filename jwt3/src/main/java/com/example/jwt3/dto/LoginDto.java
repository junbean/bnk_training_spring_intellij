package com.example.jwt3.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginDto {
    private String username;
    private String password;
}
