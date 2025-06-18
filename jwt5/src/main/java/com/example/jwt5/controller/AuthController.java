package com.example.jwt5.controller;

import com.example.jwt5.RefreshTokenStore;
import com.example.jwt5.dto.LoginRequest;
import com.example.jwt5.dto.LoginResponse;
import com.example.jwt5.dto.RefreshRequest;
import com.example.jwt5.dto.TokenResponse;
import com.example.jwt5.utils.JWTUtil;
import lombok.Getter;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final JWTUtil jwtUtil;
    private final RefreshTokenStore refreshTokenStore;

    public AuthController(JWTUtil jwtUtil, RefreshTokenStore refreshTokenStore) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenStore = refreshTokenStore;
    }

    // 로그인 인증 절차
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. 아이디, 비밀번호 검증 (여기선 예제로 간단히 처리)
        if (!"testuser".equals(request.getUsername()) || !"1234".equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // 2. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());

        // 3. RefreshToken 저장
        refreshTokenStore.saveRefreshToken(request.getUsername(), refreshToken);

        // 4. 응답
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    // refresh 토큰 재발급 절차
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. 토큰 유효성 검사
        if (!jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        // 2. 사용자 이름 추출
        String username = jwtUtil.getUsername(refreshToken);

        // 3. 저장소의 refresh token과 일치하는지 확인
        String savedRefreshToken = refreshTokenStore.getRefreshToken(username);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token mismatch or expired");
        }

        // 4. 새로운 access token 발급
        String newAccessToken = jwtUtil.generateAccessToken(username);

        return ResponseEntity.ok(new TokenResponse(newAccessToken));
    }

    @GetMapping("/access")
    public ResponseEntity<?> accessCheck(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        // 1. 헤더가 존재하는지 확인
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token");
        }

        // 2. 토큰 추출
        String token = authHeader.substring(7);

        // 3. 토큰 유효성 검사
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        // 4. 사용자 정보 추출
        String username = jwtUtil.getUsername(token);

        // 5. 인가 완료 → 사용자 맞춤 응답
        return ResponseEntity.ok("Hello, " + username + "! This is your private mypage.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 토큰 유효성 검사
        if (!jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // 사용자 이름 추출
        String username = jwtUtil.getUsername(refreshToken);

        // 저장소에서 삭제
        refreshTokenStore.deleteRefreshToken(username);

        return ResponseEntity.ok("Logged out successfully");
    }

}


/*

{
    "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJpYXQiOjE3NTAyMzQ1MTMsImV4cCI6MTc1MDIzNTQxM30.GzGbirvZRMeTfUL2XjtglpAYLsP3CgMsHNe9PF3ql_hsWRigjLEld1KeMVKFdrN6",
    "refreshToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJSZWZyZXNoVG9rZW4iLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwiaWF0IjoxNzUwMjM0NTEzLCJleHAiOjE3NTA4MzkzMTN9.ri4_PXJug4xk9QFSaYAzSloIJrRGXfJH3n7DVIjCyzd5fe2yYajjNNZDN8c5j1DN"
}

*/