package com.example.community.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommunityException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public CommunityException(String message, HttpStatus status) {
        super(message);

        this.message = message;
        this.status = status;
    }
}