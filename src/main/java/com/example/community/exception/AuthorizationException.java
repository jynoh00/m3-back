package com.example.community.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends CommunityException{
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}