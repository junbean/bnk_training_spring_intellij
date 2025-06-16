package com.example.jwt2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt2.dto.LoginDto;
import com.example.jwt2.utils.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class JwtTestController {
	
	@Value("${spring.jwt.secret}")
	private String secretKey;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@PostMapping("/login")
	public String login(
		@RequestBody LoginDto loginDto, 
		// HttpServletRequest request,	// session을 쓸 일이 없으니까 request객체는 안씀 
		HttpServletResponse response
	) {
		// 로그인 성공
		if(loginDto.getUsername().equals("user01") && loginDto.getPassword().equals("1234")) {
			// 토큰 준비
			String token = makeJwt(loginDto.getUsername());

			// 토큰 발행
			response.setHeader("userAuth", token);
			return "good";
		}
		
		return "bad";
	}
	
	@GetMapping("/user")
	public String getUserInfo(HttpServletRequest request) {
		String userAuth = request.getHeader("userAuth");
		
		if(userAuth == null || userAuth.isEmpty()) {
			return "bad1";
		}

		// split(" ")으로 나누면 "Bearer xxx.yyy.zzz" → ["Bearer", "xxx.yyy.zzz"]
		String[] jwtList = userAuth.split(" ");
		if(jwtList.length != 2 || !jwtList[0].equals("Bearer")) {
			return "bad2";
		}
		
		String username = jwtUtil.getUsername(jwtList[1]);
		
		return username;
	}

	private String makeJwt(String username) {
		return "Bearer " + jwtUtil.createJwt(username);
	}
	
	
	/*
	   // split(" ")으로 나누면 "Bearer xxx.yyy.zzz" → ["Bearer", "xxx.yyy.zzz"]
       String[] parts = userAuth.split(" ");
       if (parts.length != 2 || !parts[0].equals("Bearer")) {
           return "잘못된 토큰 형식";
       }

       String jwt = parts[1]; // JWT만 추출됨
       return "추출된 토큰: " + jwt; 
	*/
	 
	
}
