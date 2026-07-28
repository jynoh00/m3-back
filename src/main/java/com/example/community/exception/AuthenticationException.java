package com.example.community.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends CommunityException{
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}