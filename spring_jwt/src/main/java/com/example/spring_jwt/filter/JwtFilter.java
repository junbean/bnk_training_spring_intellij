package com.example.spring_jwt.filter;

import com.example.spring_jwt.util.JWTUtil;
import jakarta.servlet.*;
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

        // 1. Authorization 헤더 추출
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization 헤더: " + authHeader);

        // 2. 유효한 Bearer 토큰인지 확인
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("인증 실패: Authorization 헤더가 없거나 형식이 잘못되었습니다.");
            return;
        }

        // 3. 토큰 추출
        String token = authHeader.substring(7);

        // 4. 토큰 유효성 검사
        if (!jwtUtil.isValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("인증 실패: 토큰이 유효하지 않습니다.");
            return;
        }

        // 5. 사용자 정보 추출 (원하면 request에 담아 다음 컨트롤러에 전달)
        String username = jwtUtil.getUsername(token);
        request.setAttribute("username", username);
        System.out.println("인증된 사용자: " + username);

        // 6. 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }
}

/*

궁금한 것들
    1. Filter와 OncePerRequestFilter 차이와 목적
        1. javax.servlet.Filter (기본 서블릿 필터)
            Java Servlet API에 정의된 표준 필터 인터페이스
            웹 애플리케이션 전반에 적용 가능한 범용 필터
            //  ====================================================
            doFilter(ServletRequest, ServletResponse, FilterChain) 메서드를 오버라이드해야 함
                public interface Filter {
                    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain);
                }
            }
            //  ====================================================

        2. OncePerRequestFilter (Spring 전용 추상 클래스)
            Spring이 제공하는 Filter의 확장 클래스
            이름 그대로 "요청당 한 번만 실행되도록 보장"
            내부적으로 HttpServletRequest/HttpServletResponse를 이미 사용하게 되어 있음
            doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)을 오버라이드
            //  ====================================================
            public abstract class OncePerRequestFilter implements Filter {
                @Override
                public final void doFilter(...) {
                    // 내부적으로 한 번만 실행되도록 체크한 후,
                    doFilterInternal(request, response, chain);
                }

                protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain);
            }
            //  ====================================================

    2. 언제 어느 것을 써야 하나?
        | 구분        | Filter (interface)                  | OncePerRequestFilter (Spring class)                        |
        | -------    | -------------------------            | ---------------------------------------------------------- |
        | 정의 위치   | Java Servlet API 표준                 | Spring Web 전용                                              |
        | 목적       | 모든 요청마다 실행                       | 요청당 한 번만 실행되도록 보장                                          |
        | 설정       | web.xml, Java Config 등 자유           | 주로 `FilterRegistrationBean` 또는 Spring Security FilterChain |
        | 가독성      | 직접 캐스팅 필요                        | `HttpServletRequest/Response`로 바로 사용 가능                    |
        | ✅ 실무 추천 | ❌ 한 요청에서 두 번 실행될 수 있어 위험  | ✅ Spring에서는 이걸 더 많이 씀                                      |

    3. 메서드 차이 : doFilter() vs doFilterInternal()
        | 메서드                  | 위치                           | 설명                                        |
        | -------------------- | ---------------------------- | ----------------------------------------- |
        | `doFilter()`         | Filter 인터페이스                 | 기본 필터 로직 처리 (request/response 수동 형변환 필요)  |
        | `doFilter()`         | OncePerRequestFilter (final) | 내부적으로 중복 실행을 막고 `doFilterInternal()`을 호출함 |
        | `doFilterInternal()` | OncePerRequestFilter         | 우리가 직접 구현하는 실제 필터 로직 (타입 안전)              |


필터는 어디에 적용되는 것인가?
    OncePerRequestFilter는 Spring 서버 내부의 HTTP 요청 처리 흐름 중 필터에 해당합니다.

    즉, 클라이언트(Flutter, 웹, Postman 등)에서 어떤 요청이 오든,
    → 서버에서 받은 HTTP 요청을 필터링할 때 딱 한 번 실행되도록 보장합니다.



"Spring Web 전용"의 진짜 의미
    "Web 전용"이란 말은…
        OncePerRequestFilter는 Spring MVC 기반 Web 요청 처리에만 적용되는 추상 클래스라는 의미입니다.

        즉, WebFlux (reactive)에는 안 쓰이고,

        Spring이 서블릿 기반 요청(HttpServletRequest) 을 감지하고 동작한다는 뜻이지,

        "웹브라우저에서만 동작한다"는 의미는 절대 아닙니다.


Flutter 앱 → JWT 인증 흐름
    [ Flutter 앱 ]
      ↓ Authorization: Bearer eyJhbGciOiJIUzI1...
    [ Spring 서버 ]
      → OncePerRequestFilter가 요청 가로채서 토큰 유효성 검사
      → 유효하면 다음 Controller로 전달
      → 응답 반환 (예: 200 OK or 401 Unauthorized)


모바일 앱에서 JWT 인증할 때 유의사항
    토큰 저장 위치: SecureStorage (Flutter용 패키지 있음)

    토큰 전송 방식: Authorization: Bearer {token}

    Access Token 만료 시 Refresh Token을 통한 갱신 로직 구현 권장

    HTTPS 사용 필수 (JWT 탈취 방지)


*/







/*


public class JwtFilter implements Filter {

    private final JWTUtil jwtUtil;

    public JwtFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        // Authorization 헤더에서 토큰 추출
        String authHeader = httpReq.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer " 제거

            if (jwtUtil.isValid(token)) {
                // ✅ 유효한 경우 → 사용자 정보 꺼내기 (optional)
                String username = jwtUtil.getUsername(token);
                System.out.println("인증된 사용자: " + username);

                // 여기에 사용자 정보 전달 로직을 추가하고 싶다면 request.setAttribute() 사용 가능
                httpReq.setAttribute("username", username);

                // 다음 필터로 진행
                chain.doFilter(request, response);
                return;
            }
        }

        // ❌ 토큰이 없거나 유효하지 않음
        httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpRes.getWriter().write("Unauthorized: 유효하지 않은 JWT입니다.");
    }
}

*/