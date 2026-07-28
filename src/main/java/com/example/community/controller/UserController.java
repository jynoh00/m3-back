package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.common.ResponseMessage;
import com.example.community.security.TokenProvider;
import com.example.community.service.UserService;
import com.example.community.dto.JoinRequestDTO;
import com.example.community.dto.UpdatePasswordDTO;
import com.example.community.dto.UpdateProfileRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController { // 사용자 정보 관련 요청 처리
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @GetMapping("/join")
    public ResponseEntity<?> getJoinForm() {
        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.JOIN_PAGE_LOAD.getMessage()));
    }

    @PostMapping("/join")
    public ResponseEntity<?> tryJoin(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        Long userId = userService.joinProcess(joinRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseFormat.of(ResponseMessage.JOIN_SUCCESS.getMessage(),
                        Map.of(
                                "user_id", userId
                        ))
        );
    }

    @GetMapping("/me/profile")
    public ResponseEntity<?> getProfileEditForm(HttpServletRequest request) {
        Long userId = tokenProvider.getUserId(request);
        Map<String, Object> userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.USER_PROFILE_EDIT_PAGE_LOAD.getMessage(), userInfo)
        );
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody() UpdateProfileRequestDTO updateProfileRequestDTO,
            HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        Map<String, Object> userInfo = userService.updateProfileProcess(
                updateProfileRequestDTO.getUserNewNickname(),
                updateProfileRequestDTO.getUserNewImage(),
                userId
        );

        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.PROFILE_UPDATE_SUCCESS.getMessage(), userInfo)
        );
    }

    @GetMapping("/me/password")
    public ResponseEntity<?> getPasswordEditForm() {
        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.USER_PASSWORD_EDIT_PAGE_LOAD.getMessage()));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody() UpdatePasswordDTO updatePasswordDTO,
            HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        userService.updatePasswordProcess(updatePasswordDTO, userId);
        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.PASSWORD_UPDATE_SUCCESS.getMessage()));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> tryWithdraw(HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        userService.withdrawProcess(userId);
        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.USER_DELETE_SUCCESS.getMessage()));
    }
}