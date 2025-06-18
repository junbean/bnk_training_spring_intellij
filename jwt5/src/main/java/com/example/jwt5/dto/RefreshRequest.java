package com.example.jwt5.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
