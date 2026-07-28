package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.common.UserRole;
import com.example.community.dto.LoginRequestDTO;
import com.example.community.entity.main.auth.RefreshToken;
import com.example.community.entity.main.user.User;
import com.example.community.exception.AuthenticationException;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.main.auth.RefreshTokenRepository;
import com.example.community.repository.main.user.UserRepository;
import com.example.community.security.PasswordEncoder;
import com.example.community.security.RefreshTokenInfo;
import com.example.community.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.example.community.TestUserConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String WRONG_EMAIL = "wrong@test.com";
    private static final String WRONG_PASSWORD = "Wrongpass1!";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    private static final String BEARER_REFRESH_TOKEN = "Bearer " + REFRESH_TOKEN;
    private static final String INVALID_REFRESH_TOKEN_MESSAGE = "invalid_refresh_token";

    private static final String RESPONSE_USER_ID = "user_id";
    private static final String RESPONSE_EMAIL = "user_email";
    private static final String RESPONSE_NICKNAME = "user_nickname";
    private static final String RESPONSE_IMAGE = "user_image";
    private static final String RESPONSE_ACCESS_TOKEN = "access_token";
    private static final String RESPONSE_REFRESH_TOKEN = "refresh_token";

    private static final String PASSWORD_FAILED_MESSAGE = "password_failed";

    @Mock
    UserRepository userRepository;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    AuthService authService;

    @Test
    void 사용자는_올바른_이메일과_비밀번호로_로그인할_수_있다() {
        User user = NORMAL.toUser();
        LoginRequestDTO request = loginRequest(NORMAL.email, NORMAL.password);

        when(userRepository.findByEmail(NORMAL.email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(NORMAL.password, NORMAL.encodedPassword)).thenReturn(true);
        when(tokenProvider.createAccessToken(NORMAL.id, UserRole.USER.getRole())).thenReturn(ACCESS_TOKEN);
        when(tokenProvider.createRefreshToken(NORMAL.id))
                .thenReturn(new RefreshTokenInfo(REFRESH_TOKEN, LocalDateTime.now().plusDays(1)));

        Map<String, Object> result = authService.loginProcess(request);

        assertThat(result.get(RESPONSE_USER_ID)).isEqualTo(NORMAL.id);
        assertThat(result.get(RESPONSE_EMAIL)).isEqualTo(NORMAL.email);
        assertThat(result.get(RESPONSE_NICKNAME)).isEqualTo(NORMAL.nickname);
        assertThat(result.get(RESPONSE_IMAGE)).isNotNull();
        assertThat(result.get(RESPONSE_ACCESS_TOKEN)).isEqualTo(ACCESS_TOKEN);
        assertThat(result.get(RESPONSE_REFRESH_TOKEN)).isEqualTo(REFRESH_TOKEN);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getToken()).isEqualTo(REFRESH_TOKEN);
    }

    @Test
    void 로그인_시_존재하지_않는_이메일이면_실패한다() {
        LoginRequestDTO request = loginRequest(WRONG_EMAIL, NORMAL.password);

        when(userRepository.findByEmail(WRONG_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.loginProcess(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ExceptionMessage.USER_EMAIL_NOT_FOUND.getMessage());

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않으면_실패한다() {
        User user = NORMAL.toUser();
        LoginRequestDTO request = loginRequest(NORMAL.email, WRONG_PASSWORD);

        when(userRepository.findByEmail(NORMAL.email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(WRONG_PASSWORD, NORMAL.encodedPassword)).thenReturn(false);

        assertThatThrownBy(() -> authService.loginProcess(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(PASSWORD_FAILED_MESSAGE);

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void 로그아웃_시_Authorization_헤더가_null이면_실패한다() {
        assertThatThrownBy(() -> authService.logoutProcess(null))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_REFRESH_TOKEN_MESSAGE);

        verify(refreshTokenRepository, never()).findByToken(anyString());
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void 로그아웃_시_Authorization_헤더가_Bearer_형식이_아니면_실패한다() {
        assertThatThrownBy(() -> authService.logoutProcess(REFRESH_TOKEN))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_REFRESH_TOKEN_MESSAGE);

        verify(refreshTokenRepository, never()).findByToken(anyString());
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void 로그아웃_시_리프레시_토큰이_저장소에_없으면_실패한다() {
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logoutProcess(BEARER_REFRESH_TOKEN))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_REFRESH_TOKEN_MESSAGE);

        verify(refreshTokenRepository).findByToken(REFRESH_TOKEN);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void 로그아웃_성공_시_리프레시_토큰을_삭제한다() {
        User user = NORMAL.toUser();
        RefreshToken refreshToken = new RefreshToken(
                user,
                LocalDateTime.now().plusDays(1),
                REFRESH_TOKEN
        );

        when(refreshTokenRepository.findByToken(REFRESH_TOKEN))
                .thenReturn(Optional.of(refreshToken));

        authService.logoutProcess(BEARER_REFRESH_TOKEN);

        verify(refreshTokenRepository).findByToken(REFRESH_TOKEN);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    private LoginRequestDTO loginRequest(String email, String password) {
        LoginRequestDTO request = new LoginRequestDTO();
        ReflectionTestUtils.setField(request, "userEmail", email);
        ReflectionTestUtils.setField(request, "userPassword", password);
        return request;
    }
}