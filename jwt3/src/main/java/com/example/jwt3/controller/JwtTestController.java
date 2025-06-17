package com.example.jwt3.controller;

import com.example.jwt3.dto.LoginDto;
import com.example.jwt3.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RestController
public class JwtTestController {
	
	@Value("${spring.jwt.secret}")
	private String secretKey;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@PostMapping("/login")
	public String login(
		@RequestBody LoginDto loginDto,
		HttpServletResponse response
	) {
		// 로그인 성공
		if(loginDto.getUsername().equals("user01") && loginDto.getPassword().equals("1234")) {
			// 토큰 준비
			String userEmail = "user01@email.com";
			String token = makeJwt(loginDto.getUsername(), userEmail);

			// 토큰 발행
			response.setHeader("userAuth", token);
			return "good";
		}
		
		return "bad";
	}
	
	@GetMapping("/user")
	public String getUserInfo(HttpServletRequest request) {
		// 요청 헤더에 담긴 userAuth값을 가져온다
		String userAuth = request.getHeader("userAuth");

		// 헤더가 없거나 비어 있다면 → 유효하지 않은 요청으로 간주
		if(userAuth == null || userAuth.isEmpty()) {
			return "bad1";
		}

		// "Bearer"와 실제 토큰을 " "으로 나눈다
		// 두 번째 값(= 인덱스 1)인 실제 토큰(abc.def.ghi)을 꺼냄
		// 추출한 jwt에서 username과 email을 꺼낸다
		String jwt = userAuth.split(" ")[1];
		String username = jwtUtil.getUsername(jwt);
		String email = jwtUtil.getEmail(jwt);

		// JWT 검증을 위한 비밀 키(SecretKey)를 생성
		// 미리 설정한 secretKey 문자열을 바이트로 변환하여 키 객체로 사용
		SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

		// JWT 파서(Parser)를 생성
		// 이 파서는 토큰을 파싱하면서 서명까지 같이 검증해줌
		JwtParser parser = Jwts.parser()
				.verifyWith(key)
				.build();
		// 전달받은 jwt 토큰을 파싱함
		// 서명(Signature)이 유효한지 검증하고, Payload(Claims)를 추출함
		Jws<Claims> jws = parser.parseSignedClaims(jwt);	
		// 파싱된 JWT에서 Payload(=Claims)를 꺼냄
		Claims claims = jws.getPayload();

		// iat : 발급시간
		// exp : 만료시간
		Date iat = claims.getIssuedAt();	// iat
		Date exp = claims.getExpiration();	// exp

		System.out.println("발급(iat) : " + iat);
		System.out.println("만료(exp) : " + exp);

        return username + ", " + email;
	}

	private String makeJwt(String username, String email) {
		Long expTime = 1000 * 60 * 3L;	// 3분
        return "Bearer " + jwtUtil.createJwt(username, email, expTime);
	}

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