package com.example.community.security;

import com.example.community.exception.InternalServerException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class PasswordEncoder {

    public String encode(String rawPassword) {
        return hash(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return hash(rawPassword).equals(encodedPassword);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
