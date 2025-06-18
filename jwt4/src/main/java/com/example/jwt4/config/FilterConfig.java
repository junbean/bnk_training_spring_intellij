package com.example.jwt4.config;

import com.example.jwt4.filter.JwtFilter;
import com.example.jwt4.utils.JWTUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final JWTUtil jwtUtil;

    public FilterConfig(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // FilterRegistrationBean = 커스텀 필터를 스프링에 수동 등록
    // addUrlPatterns("/api/*") = /api/ 로 시작하는 요청에만 JWT 필터가 작동
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        FilterRegistrationBean<JwtFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new JwtFilter(jwtUtil));
        filterBean.addUrlPatterns("/api/*"); // 이 경로에만 필터 적용됨
        return filterBean;
    }

}

/*

✅ 1. 로그인 인증 처리
username, password를 받아서 DB에서 사용자 인증

인증 성공 시 → DB에서 username, name, role을 포함한 사용자 정보 조회 (SELECT * FROM users WHERE username = ?)

✅ 2. Access Token & Refresh Token 생성
조회한 사용자 정보를 기반으로:

Access Token: username, name, role 등 필수 정보 포함 (만료시간: 짧게, 예: 15분)

Refresh Token: 유저 식별값만 포함 or 별도 식별자 (만료시간: 길게, 예: 7일~14일)

토큰 생성 후:

Access Token은 클라이언트에 응답 헤더 or 쿠키로 전달

Refresh Token은 HttpOnly 쿠키 또는 서버 DB/Redis에 저장

✅ 3. 인증이 필요한 API 요청 처리
클라이언트는 요청 시 Access Token을 Authorization: Bearer {token} 헤더에 포함해서 전송

서버는 Access Token 유효성 검증 → 통과 시 사용자 인증 완료

✅ 4. Access Token 만료 처리
Access Token이 만료되면 서버는 401 Unauthorized 응답

클라이언트는 보관 중인 Refresh Token을 서버로 전송 (예: /refresh-token API 요청)

✅ 5. Refresh Token 검증 및 Access Token 재발급
서버는 받은 Refresh Token의 유효성 확인

만료 or 위조된 경우 → 로그인 다시 요구

유효한 경우 → 새로운 Access Token 발급 후 응답

*/