package com.example.community.common;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    INTERNAL_SERVER_ERROR("internal_server_error"),
    INVALID_INPUT_FORMAT("invalid_input_format"),
    INVALID_REQUEST_BODY("invalid_request_body"),
    USER_EMAIL_NOT_FOUND("user_email_not_found");


    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}