package com.example.jwt2.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil {
	
	private SecretKey secretKey;

    // 생성자에서 비밀키 초기화
	public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
		this.secretKey = new SecretKeySpec(
				secret.getBytes(StandardCharsets.UTF_8), 
				Jwts.SIG.HS256.key().build().getAlgorithm()
			);
		System.out.println("secretKey : " + secretKey.toString() + ", algorith : " + secretKey.getAlgorithm());
	}

    // JWT 토큰에서 사용자 정보 추출
	public String getUsername(String token) {
		String username = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token) // token에서 claim만 추출
				.getPayload()
				.get("username", String.class);
		
		return username;
	}

    // JWT 토큰 생성 - 로그인 성공 시
	public String createJwt(String username) {
		String token = Jwts.builder()
				.claim("username", username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.signWith(secretKey)
				.compact();
		
		return token;
	}
	
}

/*
보안 고려사항
현재 코드의 보안 이슈와 개선점

1. 토큰 만료 시간 설정
// 개선된 토큰 생성 메소드
public String createJwt(String username) {
    return Jwts.builder()
        .claim("username", username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간 후 만료
        .signWith(secretKey)
        .compact();
}

2. 예외 처리 강화
public String getUsername(String token) {
    try {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("username", String.class);
    } catch (JwtException e) {
        throw new RuntimeException("Invalid JWT token", e);
    }
}

3. HTTPS 사용
JWT 토큰은 민감한 정보를 포함할 수 있으므로 반드시 HTTPS 사용

4. 토큰 저장 위치
토큰 저장 위치
localStorage: XSS 공격에 취약
httpOnly 쿠키: XSS 공격 방지, CSRF 공격에 대비 필요






*/  
