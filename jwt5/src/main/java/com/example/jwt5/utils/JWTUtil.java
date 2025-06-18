package com.example.jwt5.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    // 생성자 주입
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ Access Token 생성 (짧은 수명)
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject("AccessToken")
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(60 * 15))) // 15분
                .signWith(secretKey)
                .compact();
    }

    // ✅ Refresh Token 생성 (긴 수명)
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject("RefreshToken")
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 7))) // 7일
                .signWith(secretKey)
                .compact();
    }

    // ✅ 토큰에서 username 추출
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    // ✅ 토큰 유효성 검사
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}