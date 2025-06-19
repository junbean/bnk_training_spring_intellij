package com.example.spring_jwt.config;

import com.example.spring_jwt.filter.JwtFilter;
import com.example.spring_jwt.util.JWTUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final JWTUtil jwtUtil;

    public FilterConfig(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        FilterRegistrationBean<JwtFilter> filter = new FilterRegistrationBean<>();

        filter.setFilter(new JwtFilter(jwtUtil));
        filter.addUrlPatterns("/api/mypage"); // 보호할 경로만 필터 적용

        return filter;
    }

}


/*
구체적인 흐름
    1. 요청 도착
        ↓
    2. Authorization 헤더 확인
        → 존재 & "Bearer "로 시작?
        ↓
    3. accessToken 추출
        → "Bearer " 문자열 제거
        ↓
    4. accessToken 유효성 검사
        → 서명 유효?
        → 만료 안 됨?
        ↓
    5. 통과 → 다음 컨트롤러 실행
       실패 → 401 Unauthorized



*/