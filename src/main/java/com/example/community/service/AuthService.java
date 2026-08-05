package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.dto.auth.AccessTokenResponseDTO;
import com.example.community.dto.auth.LoginRequestDTO;
import com.example.community.dto.auth.LoginResponseDTO;
import com.example.community.entity.main.auth.RefreshToken;
import com.example.community.entity.main.user.User;
import com.example.community.exception.AuthenticationException;
import com.example.community.exception.ExpiredRefreshTokenException;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.main.auth.RefreshTokenRepository;
import com.example.community.repository.main.user.UserRepository;
import com.example.community.security.BearerTokenExtractor;
import com.example.community.security.CustomPasswordEncoder;
import com.example.community.security.RefreshTokenInfo;
import com.example.community.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public LoginResponseDTO loginProcess(LoginRequestDTO loginRequestDTO) {
        // 사용자 확인
        User user = authenticateUser(loginRequestDTO.getUserEmail(), loginRequestDTO.getUserPassword());

        // 2. 액세스 토큰 및 리프래시 토큰 생성
        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getRole());
        RefreshTokenInfo refreshTokenInfo = tokenProvider.createRefreshToken(user.getId());

        RefreshToken refreshToken = new RefreshToken(
                user, refreshTokenInfo.getExpiresAt(), refreshTokenInfo.getValue()
        );
        refreshTokenRepository.save(refreshToken);

        return new LoginResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getImage(),
                accessToken,
                refreshTokenInfo.getValue()
        );
    }

    @Transactional(noRollbackFor = ExpiredRefreshTokenException.class)
    public AccessTokenResponseDTO refreshAccessToken(String authorization) {
        String refreshTokenValue = extractRefreshTokenValue(authorization);

        if (!tokenProvider.validateRefreshToken(refreshTokenValue)) {
            throw new AuthenticationException(ExceptionMessage.INVALID_REFRESH_TOKEN.getMessage());
        }

        Long userId = tokenProvider.getUserId(refreshTokenValue);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new AuthenticationException(ExceptionMessage.INVALID_REFRESH_TOKEN.getMessage()));

        if (!refreshToken.getUser().getId().equals(userId)) {
            throw new AuthenticationException(ExceptionMessage.INVALID_REFRESH_TOKEN.getMessage());
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ExpiredRefreshTokenException(ExceptionMessage.EXPIRED_REFRESH_TOKEN.getMessage()); // 해당 예외 발생 시 롤백 안되게 설정
        }

        String accessToken = tokenProvider.createAccessToken(userId, refreshToken.getUser().getRole());

        return new AccessTokenResponseDTO(accessToken);
    }

    private User authenticateUser(String userEmail, String userPassword) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_EMAIL_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            throw new AuthenticationException(ExceptionMessage.PASSWORD_FAILED.getMessage());
        }

        return user;
    }

    @Transactional
    public void logoutProcess(String authorization) {
        String refreshTokenValue = extractRefreshTokenValue(authorization);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new AuthenticationException(ExceptionMessage.INVALID_REFRESH_TOKEN.getMessage()));

        refreshTokenRepository.delete(refreshToken);
    }

    private String extractRefreshTokenValue(String authorization) {
        return BearerTokenExtractor.extract(authorization)
                .orElseThrow(() -> new AuthenticationException(ExceptionMessage.INVALID_REFRESH_TOKEN.getMessage()));
    }
}