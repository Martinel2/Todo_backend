package com.todo.todo.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "mySecretKey";  // 비밀 키
    private static final long ACCESS_TOKEN_EXPIRATION = 3600 * 1000;  // 액세스 토큰 만료 시간 (1시간)
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 3600 * 1000; // 리프레시 토큰 만료 (7일)

    // JWT 액세스 토큰 생성
    public static String createAccessToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    // JWT 리프레시 토큰 생성
    public static String createRefreshToken() {
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    // JWT 검증 및 파싱
    public static DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token);
    }

    // 토큰에서 사용자 이름 추출
    public static String getUsername(String token) {
        return verifyToken(token).getSubject();
    }

    // 토큰 만료 여부 확인
    public static boolean isTokenExpired(String token) {
        return verifyToken(token).getExpiresAt().before(new Date());
    }
}
