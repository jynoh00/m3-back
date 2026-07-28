package com.example.community.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends CommunityException{
    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}