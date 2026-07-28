package com.example.community.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends CommunityException {
    public InternalServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}