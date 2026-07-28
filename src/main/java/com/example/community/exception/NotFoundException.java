package com.example.community.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CommunityException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}