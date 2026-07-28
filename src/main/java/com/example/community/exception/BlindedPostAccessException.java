package com.example.community.exception;

public class BlindedPostAccessException extends AuthorizationException {
    public BlindedPostAccessException(String message) {
        super(message);
    }
}