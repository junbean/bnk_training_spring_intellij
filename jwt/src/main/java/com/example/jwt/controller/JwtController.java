package com.example.jwt.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JwtController {

	// jwt 학습
	
	// jwt 인증 방식 - 로그인
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
		String id = request.get("username");
		String pw = request.get("password");
		if (id.equals("user01") && pw.equals("1234")) {
			return ResponseEntity.ok().header("userAuth", "Bearer user01").body("로그인성공");
		} else {
			return ResponseEntity.status(401).body("로그인실패");
		}

	}

	
	// jwt 인가 방식 - 마이페이지
	@GetMapping("/user")
	public ResponseEntity<String> user(
		@RequestHeader("userAuth") String userAuth
	) {
		// 기대 형식: "Bearer user01"
		if (userAuth != null && userAuth.startsWith("Bearer ")) {
			String username = userAuth.substring(7);
			if (username.equals("user01")) {
				return ResponseEntity.ok(username);
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}