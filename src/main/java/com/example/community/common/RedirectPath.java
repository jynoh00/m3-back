package com.example.community.common;

import lombok.Getter;

@Getter
public enum RedirectPath {
    LOGIN("/login"),
    POSTS("/posts");

    private final String path;

    RedirectPath(String path) {
        this.path = path;
    }
}