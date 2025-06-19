package com.example.spring_jwt.util;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    @Value("${spring.jwt.access-token-expiration}")  // spring.jwt 추가
    private long accessTokenExpiration;

    @Value("${spring.jwt.refresh-token-expiration}")  // spring.jwt 추가
    private long refreshTokenExpiration;

    // 비밀키 생성
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        // byte[]를 기반으로 HMAC-SHA 서명을 위한 SecretKey 객체를 생성하는 메서드
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    /*
    jwt 토큰의 구조
        header 
            어떤 서명 알고리즘을 썼는지(HS256)
        payload
            로그인한 사용자 정보 같은 데이터(username, role)
        signature
            이 토큰이 위조되지 않았다는 증거(디지털 서명)
    */

    // AccessToken 생성
    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .subject("AccessToken")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject("RefreshToken")
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 토큰에서 username 추출
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    // 토큰 유효성 검사
    public boolean isValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parse(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}


/*
// 다른 방식
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private Key signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성
    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .subject("AccessToken")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject("RefreshToken")
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    // 토큰에서 username 추출
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    // 토큰 유효성 검사
    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parse(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}

*/