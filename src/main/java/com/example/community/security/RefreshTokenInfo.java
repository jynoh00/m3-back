package com.example.community.security;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefreshTokenInfo {
    private String value;
    private LocalDateTime expiresAt;

    public RefreshTokenInfo(String value, LocalDateTime expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }
}