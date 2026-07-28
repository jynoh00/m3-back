package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.common.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController { // 루트 경로 처리
    @GetMapping("/")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.AUTHORIZED.getMessage(),
                        Map.of("redirect_url", "/posts")
                )
        );
    }
}