package com.example.spring_jwt.controller;

import com.example.spring_jwt.dto.TokenRequestDTO;
import com.example.spring_jwt.dto.TokenResponseDTO;
import com.example.spring_jwt.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final JWTUtil jwtUtil;

    public AuthController(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        // 1. 유효한 토큰인지 확인
        if (!jwtUtil.isValid(refreshToken)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body("유효하지 않은 Refresh Token입니다.");
        }

        // 2. username 꺼내기
        String username = jwtUtil.getUsername(refreshToken);

        // 3. 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateAccessToken(username, "USER");

        // 4. 반환
        return ResponseEntity.ok(new TokenResponseDTO(newAccessToken));
    }

}

// "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsInVzZXJuYW1lIjoidGVzdFVzZXIwMSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzUwMzIzMDQxLCJleHAiOjE3NTAzMjM5NDF9.-aTqyxV8ZIPtjnxYsnQe8vHD7Fcri0IOZuVTkxQN7N4",
// "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJSZWZyZXNoVG9rZW4iLCJ1c2VybmFtZSI6InRlc3RVc2VyMDEiLCJpYXQiOjE3NTAzMjMwNDEsImV4cCI6MTc1MDkyNzg0MX0.iq8bctb0_EFqzeB8dZysTH2OfnSMs2omTPLKRB90ebg"


/*

궁금증

1. Access Token은 헤더에, Refresh Token은 왜 바디에?
    결론 요약
        🔐 Access Token은 헤더에 넣는 게 표준적이고 권장 방식이며,
        🔐 Refresh Token은 바디 또는 HttpOnly 쿠키에 넣는 것이 일반적입니다.
    Access Token → Authorization 헤더에 보내는 이유
        | 항목               | 설명                                                         |
        | ---------------- | ---------------------------------------------------------- |
        | ✅ 표준 방식          | OAuth2, REST 표준에서 Access Token은 Authorization 헤더 사용        |
        | ✅ HTTP 요청이 깔끔함   | GET, POST 등 모든 요청에서 동일하게 처리 가능                             |
        | ✅ 빠르게 인증 필터에서 추출 | 서버에서 필터나 인터셉터로 쉽게 검사 가능                                    |
        | ❌ 보안 취약성 있음      | JS에서 접근 가능하므로 XSS에 노출될 수 있음 → SPA에서는 HttpOnly cookie를 더 권장 |
    Refresh Token → Body나 HttpOnly Cookie로 보내는 이유
        | 이유                       | 설명                               |
        | ------------------------ | -------------------------------- |
        | ✅ 서버가 요청 본문에서 안전하게 추출 가능 | POST 요청에서 body 파싱만으로 처리 가능       |
        | ✅ 보안상 상대적으로 안전함          | 토큰이 URL에 노출되지 않음                 |
        | ❌ 여전히 JS에서 접근 가능         | 앱이 해킹당하면 Refresh Token도 탈취 위험 있음 |
    왜 Refresh Token을 헤더에 안 넣을까?
        Authorization 헤더는 Access Token만 사용하는 걸로 암묵적 규칙이 있습니다.
        Refresh Token까지 섞어버리면 → 필터 로직이나 인증 처리 구분이 복잡해져요.

2. 왜 Access Token 앞에 'Bearer '를 붙여야 하지?
    결론부터 말하자면
        "Bearer {token}"은 HTTP 인증 방식 중 하나인
        Bearer 인증 스킴(Bearer authentication scheme)에 따른 국제 표준 규칙입니다.
        → "이 헤더는 인증을 위한 토큰이고, Bearer 방식으로 해석해야 한다"는 의미를 전달하는 역할입니다.

        Bearer, Basic, Digest, OAuth 등 여러 인증 타입이 존재함

    왜 Bearer를 명시해야 하나?
        | 이유                                      | 설명                                                                 |
        | --------------------------------------- | ------------------------------------------------------------------ |
        | ✅ 표준 호환성                                | HTTP 클라이언트/서버가 이 인증 방식이 **Bearer 토큰 방식**임을 알 수 있음                  |
        | ✅ 확장성                                   | 나중에 다른 인증 방식(Basic, OAuth 등)과 혼동되지 않도록 명확히 구분                      |
        | ✅ 미들웨어/필터/보안 라이브러리들이 `Bearer`로 분기 처리 가능 |                                                                    |
        | ✅ 에러 대응                                 | 헤더가 "Bearer"로 시작하지 않으면 → 401 응답 + `WWW-Authenticate: Bearer` 안내 가능 |
    프론트에서 Bearer를 붙이는 이유
        서버가 헤더에서 Bearer 문자열을 기준으로 토큰을 파싱하기 때문
        표준을 따르지 않으면 → JwtFilter에서 startsWith("Bearer ") 조건에 걸려 401 Unauthorized 발생
    한 줄 요약:
        "Bearer"는 단순한 접두사가 아니라,
        "이건 인증 토큰이고 Bearer 방식으로 처리해달라"는 의미의 공식 신호입니다.


3. 종합적인 jwt 로그인 절차에 대한 전체적인 흐름

*/