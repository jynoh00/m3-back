package com.example.community.exception;

import org.springframework.http.HttpStatus;

public class ExpiredRefreshTokenException extends CommunityException {
    public ExpiredRefreshTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}