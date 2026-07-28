package com.example.community.security;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

public interface TokenProvider {
    public String createAccessToken(Long userId, String role);
    public RefreshTokenInfo createRefreshToken(Long userId);
    public boolean validateAccessToken(String token);
    public boolean validateRefreshToken(String token);
    public Long getUserId(String token);
    public Long getUserId(HttpServletRequest request);
    public String getRole(String token);
}