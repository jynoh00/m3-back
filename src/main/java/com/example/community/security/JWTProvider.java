package com.example.community.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JWTProvider implements TokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_MILLIS = 1000L * 60 * 30;
    private static final long REFRESH_TOKEN_EXPIRE_MILLIS = 1000L * 60 * 60 * 24 * 7;

    private final SecretKey secretKey;
    // 따로 빼서 관리

    public JWTProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(Long userId, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_MILLIS);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "access")
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public RefreshTokenInfo createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_MILLIS);

        String tokenValue = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

        LocalDateTime expiresAt = expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return new RefreshTokenInfo(tokenValue, expiresAt);
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);

            return "access".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);

            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long getUserId(HttpServletRequest request) {
        return getUserId(request.getHeader(HttpHeaders.AUTHORIZATION).substring(7));
    }

    @Override
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    @Override
    public String getRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}