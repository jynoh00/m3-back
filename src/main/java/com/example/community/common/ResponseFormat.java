package com.example.community.common;

import lombok.Getter;

@Getter
public class
ResponseFormat<T> {
    private final String message;
    private final T data;

    private ResponseFormat(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseFormat<T> of(String message, T data) {
        return new ResponseFormat<>(message, data);
    }

    public static ResponseFormat<Void> of(String message) {
        return new ResponseFormat<>(message, null);
    }
}