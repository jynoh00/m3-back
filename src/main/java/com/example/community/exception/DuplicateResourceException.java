package com.example.community.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends CommunityException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}