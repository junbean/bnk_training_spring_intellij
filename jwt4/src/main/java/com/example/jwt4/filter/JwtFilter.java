package com.example.jwt4.filter;

import com.example.jwt4.utils.JWTUtil;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JwtFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 클라이언트가 보낸 JWT 토큰을 헤더에서 추출
        String authorization = request.getHeader("Authorization");
        System.out.println("토큰 : " + authorization);

        // 1. Authorization 헤더가 없거나 형식이 다르면 필터 중단
        // startsWith("Bearer ") = Bearer {token} 형식인지 확인
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 2. 토큰 추출
        // substring(7) = Bearer 뒤의 토큰만 추출
        String token = authorization.substring(7);

        // 3. 토큰 유효성 검사
        // jwtUtil.isExpired(token) = 토큰 만료 여부 확인
        if (jwtUtil.isExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 4. 유저 정보 추출
        // getUsername(token) = 유저 정보를 확인 (이후 인증 처리도 가능)
        String username = jwtUtil.getUsername(token);
        System.out.println("인증된 사용자 : " + username);

        // 5. 필터 계속 진행
        // 현재 필터의 처리를 마친 후, 다음 필터나 컨트롤러로 요청/응답 흐름을 전달하는 메서드
        filterChain.doFilter(request, response);
    }
}
