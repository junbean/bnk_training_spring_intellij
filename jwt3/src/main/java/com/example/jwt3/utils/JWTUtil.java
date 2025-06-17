package com.example.jwt3.utils;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private final SecretKey secretKey;

    // 비밀키 문자열을 SecretKey 형태로 만든다
    // 생성자를 통해
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    // JWT 토큰에서 사용자 정보 추출
    public String getUsername(String token) {
        return Jwts.parser()    // 서명 거증 + ㅊ구
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    // JWT 토큰에서 사용자 정보 추출
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    // JWT 토큰 생성 - 로그인 성공 시 : 시간 계산 - 1000 * 60 * 3L = 3분
    public String createJwt(String username, String email, Long expirationMs) {
        return Jwts.builder()                   // JWT 생성 시작
                .claim("username", username)    // JWT에 사용자 이름 추가
                .claim("email", email)          // JWT에 이메일 추가
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰이 생성된 시간을 설정
                .expiration(new Date(System.currentTimeMillis() + expirationMs))    // 토큰 만료 시간을 설정
                .signWith(secretKey)        // JWT에 서명을 추가 (비밀키를 사용하여 위조 방지)
                .compact();             // 토큰을 문자열 형태로 생성
    }

}
/*

클라이언트가 요청
// 그런데 jwt가 만료가 됐다면 응답을 401코드로 응답한다
그러하ㄹ

// ==========================

기본 빌더 생성
java
복사
편집
Jwts.builder()
JWT를 생성하는 빌더 객체 반환

이후 메서드들을 체이닝하여 구성

1. setHeader(Map<String, Object> headerClaims)
JWT의 Header 부분을 수동으로 설정

일반적으로 생략 가능 (기본으로 alg, typ 설정됨)

java
복사
편집
.setHeader(Map.of("alg", "HS256", "typ", "JWT"))
📌 보통은 setHeaderParam()을 사용하는 것이 일반적

2. setHeaderParam(String name, Object value)
Header에 개별 파라미터 설정

java
복사
편집
.setHeaderParam("kid", "myKeyId123")
kid: Key ID (서버가 키를 여러 개 사용할 경우 식별용)

3. setClaims(Map<String, Object> claims)
Payload 전체를 한 번에 설정

기존 개별 claim()보다 전체 덮어쓰기

java
복사
편집
Map<String, Object> claims = new HashMap<>();
claims.put("username", "user01");
claims.put("role", "USER");

.setClaims(claims)
⚠️ setClaims()를 호출하면 이후 .claim() 호출은 무시됨

4. claim(String name, Object value)
Payload에 개별 Claim 추가

java
복사
편집
.claim("email", "user01@example.com")
.claim("role", "ADMIN")
✅ 가장 자주 사용되는 메서드 중 하나

5. setSubject(String subject)
토큰의 주제(Subject)를 설정

일반적으로 사용자 ID 또는 주요 식별자로 사용됨

java
복사
편집
.setSubject("user01")
6. setIssuer(String issuer)
토큰 발급자(회사/서비스 이름 등)

java
복사
편집
.setIssuer("MyCompany")
7. setAudience(String audience)
수신자 정보

이 토큰이 어떤 대상 시스템을 위한 것인지 명시

java
복사
편집
.setAudience("mobile-app")
8. setIssuedAt(Date issuedAt)
토큰 발행 시간 설정

java
복사
편집
.setIssuedAt(new Date())
보통 new Date(System.currentTimeMillis()) 사용

9. setNotBefore(Date notBefore)
토큰이 언제부터 유효한지 설정

java
복사
편집
.setNotBefore(new Date(System.currentTimeMillis() + 1000 * 60)) // 1분 후부터 유효
10. setExpiration(Date expiration)
토큰 만료 시간 설정

java
복사
편집
.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3)) // 3분 후 만료
⚠️ 시간 초과 시 예외(ExpiredJwtException) 발생

11. signWith(Key key)
서명 키를 사용하여 토큰을 서명(Signing)

무결성 검증을 위해 반드시 사용해야 함

java
복사
편집
.signWith(secretKey)
secretKey는 io.jsonwebtoken.security.Keys로 생성한 Key 객체

java
복사
편집
Key key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
12. signWith(Key key, SignatureAlgorithm alg)
명시적으로 알고리즘까지 지정해서 서명

java
복사
편집
.signWith(secretKey, SignatureAlgorithm.HS256)
HS256, RS256, ES256 등 사용 가능

13. compact()
모든 설정을 마치고 최종 JWT 문자열 생성

java
복사
편집
.compact()
이 메서드를 호출해야 최종 토큰(String)이 반환됨

📝 추가 요약: 자주 쓰는 조합 예시
java
복사
편집
String token = Jwts.builder()
    .setSubject("user01")
    .claim("email", "user01@example.com")
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) // 5분 유효
    .signWith(secretKey)
    .compact();

*/