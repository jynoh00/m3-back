package com.example.community.service;

import com.example.community.common.UserRole;
import com.example.community.dto.LoginRequestDTO;
import com.example.community.entity.main.auth.RefreshToken;
import com.example.community.entity.main.user.User;
import com.example.community.exception.AuthenticationException;
import com.example.community.exception.ExpiredRefreshTokenException;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.main.auth.RefreshTokenRepository;
import com.example.community.repository.main.user.UserRepository;
import com.example.community.security.PasswordEncoder;
import com.example.community.security.RefreshTokenInfo;
import com.example.community.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public Map<String, Object> loginProcess(LoginRequestDTO loginRequestDTO) {
        // 사용자 확인
        User user = authenticateUser(loginRequestDTO.getUserEmail(), loginRequestDTO.getUserPassword());

        // 2. 액세스 토큰 및 리프래시 토큰 생성
        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole());
        RefreshTokenInfo refreshTokenInfo = tokenProvider.createRefreshToken(user.getId());

        RefreshToken refreshToken = new RefreshToken(
                user, refreshTokenInfo.getExpiresAt(), refreshTokenInfo.getValue()
        );
        refreshTokenRepository.save(refreshToken);

        // 응답 생성 - 응답 DTO로 관리하도록 변경할 것
        Map<String, Object> response = new HashMap<>();
        response.put("user_nickname", user.getNickname());
        response.put("user_email", user.getEmail());
        response.put("user_image", user.getImage());
        response.put("access_token", accessToken);
        response.put("refresh_token", refreshTokenInfo.getValue());
        response.put("user_id", user.getId());
        return response;
    }

    @Transactional(noRollbackFor = ExpiredRefreshTokenException.class)
    public String refreshAccessToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AuthenticationException("invalid_refresh_token");
        }

        String refreshTokenValue = authorization.substring(7);

        if (!tokenProvider.validateRefreshToken(refreshTokenValue)) {
            throw new AuthenticationException("invalid_refresh_token");
        }

        Long userId = tokenProvider.getUserId(refreshTokenValue);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new AuthenticationException("invalid_refresh_token"));

        if (!refreshToken.getUser().getId().equals(userId)) {
            throw new AuthenticationException("invalid_refresh_token");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ExpiredRefreshTokenException("expired_refresh_token"); // 해당 예외 발생 시 롤백 안되게 설정
        }

        return tokenProvider.createAccessToken(userId, refreshToken.getUser().getRole());
    }

    private User authenticateUser(String userEmail, String userPassword) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("user_email_not_found"));

        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            throw new AuthenticationException("password_failed");
        }

        return user;
    }

    @Transactional
    public void logoutProcess(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AuthenticationException("invalid_refresh_token");
        }

        String refreshTokenValue = authorization.substring(7);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new AuthenticationException("invalid_refresh_token"));

        refreshTokenRepository.delete(refreshToken);
    }
}