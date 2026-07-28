package com.example.community.handler;

import com.example.community.common.ExceptionMessage;
import com.example.community.common.ResponseFormat;
import com.example.community.exception.CommunityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CommunityExceptionHandler {

    // 400 - 잘못된 형식의 http 요청
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseFormat<Void>> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = ExceptionMessage.INVALID_REQUEST_BODY.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(ResponseFormat.of(message));
    }

    // 400 - Bean Validation 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseFormat<Void>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = ExceptionMessage.INVALID_INPUT_FORMAT.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(ResponseFormat.of(message));
    }

    // 404 - 존재하지 않는 URL
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> noResourceFoundException(NoResourceFoundException e) {
        String message = "not_found_url";
        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status).body(ResponseFormat.of(message,
                Map.of("redirect_url", "/login")));
    }

    @ExceptionHandler(CommunityException.class)
    public ResponseEntity<ResponseFormat<Void>> communityException(CommunityException e) {
        String message = e.getMessage();
        HttpStatus status = e.getStatus();

        return ResponseEntity.status(status).body(ResponseFormat.of(message));
    }

    // 500 - unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseFormat<Void>> unexpectedException(Exception e) {

        log.error("Unexpected exception occurred", e);

        String message = ExceptionMessage.INTERNAL_SERVER_ERROR.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(ResponseFormat.of(message));
    }
}