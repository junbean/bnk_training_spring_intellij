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

	// Jwt 토큰 발행
	public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
		this.secretKey = new SecretKeySpec(
				secret.getBytes(StandardCharsets.UTF_8), 
				Jwts.SIG.HS256.key().build().getAlgorithm()
			);
		System.out.println("secretKey : " + secretKey.toString() + ", algorith : " + secretKey.getAlgorithm());
	}
	
	// 토큰에서 사용자 정보 추출
	public String getUsername(String token) {
		String username = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token) // token에서 claim만 추출
				.getPayload()
				.get("username", String.class);
		
		return username;
	}
	
	// 로그인 성공 시 토큰 생성
	public String createJwt(String username) {
		String token = Jwts.builder()
				.claim("username", username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.signWith(secretKey)
				.compact();
		
		return token;
	}
	
	
	
}
