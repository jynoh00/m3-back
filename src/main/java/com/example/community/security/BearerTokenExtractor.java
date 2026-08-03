package com.example.community.security;

import java.util.Optional;

public class BearerTokenExtractor {
    private static final String PREFIX = "Bearer ";

    private BearerTokenExtractor() {
    }

    public static Optional<String> extract(String authorization) {
        if (authorization == null || !authorization.startsWith(PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(authorization.substring(PREFIX.length()));
    }
}
