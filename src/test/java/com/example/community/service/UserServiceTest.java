package com.example.community.service;

import com.example.community.dto.JoinRequestDTO;
import com.example.community.dto.UpdatePasswordDTO;
import com.example.community.dto.UpdateProfileRequestDTO;
import com.example.community.entity.history.user.UserHistory;
import com.example.community.entity.main.user.User;
import com.example.community.exception.DuplicateResourceException;
import com.example.community.exception.InvalidRequestException;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.history.user.UserHistoryRepository;
import com.example.community.repository.main.auth.RefreshTokenRepository;
import com.example.community.repository.main.user.UserRepository;
import com.example.community.security.PasswordEncoder;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static com.example.community.TestUserConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String USER_NOT_FOUND_MESSAGE = "user_not_found";
    private static final String NO_USER_UPDATE_CHANGES_MESSAGE = "no_user_update_changes";

    private static final String MISMATCH_PASSWORD_MESSAGE = "mismatch_user_password";

    private static final String FIELD_EMAIL = "userEmail";
    private static final String FIELD_NICKNAME = "userNickname";
    private static final String FIELD_PASSWORD = "userPassword";
    private static final String FIELD_PASSWORD_CHECK = "userPasswordCheck";
    private static final String FIELD_IMAGE = "userImage";
    private static final String FIELD_NEW_NICKNAME = "userNewNickname";
    private static final String FIELD_NEW_PASSWORD = "userNewPassword";
    private static final String FIELD_NEW_PASSWORD_CHECK = "userNewPasswordCheck";

    private static final String RESPONSE_EMAIL = "user_email";
    private static final String RESPONSE_NICKNAME = "user_nickname";
    private static final String RESPONSE_IMAGE = "user_image";

    private static final String DUPLICATED_EMAIL_MESSAGE = "duplicated_user_email";
    private static final String DUPLICATED_NICKNAME_MESSAGE = "duplicated_user_nickname";

    @Mock
    UserRepository userRepository;

    @Mock
    UserHistoryRepository userHistoryRepository;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void 회원가입_시_이메일_비밀번호_닉네임은_필수다() {
        JoinRequestDTO request = joinRequest(null, null, null, null, null);

        assertThat(validator.validate(request))
                .extracting(v -> v.getPropertyPath().toString())
                .contains(FIELD_EMAIL, FIELD_NICKNAME, FIELD_PASSWORD, FIELD_PASSWORD_CHECK);
    }

    @Test
    void 회원가입_시_사용자_이미지를_입력하지_않아도_된다() {
        JoinRequestDTO request = NO_IMAGE.joinRequest();

        assertThat(validator.validate(request))
                .noneMatch(v -> v.getPropertyPath().toString().equals(FIELD_IMAGE));
    }

    @Test
    void 회원가입_시_이메일은_이메일_형식이어야_한다() {
        JoinRequestDTO request = joinRequest(
                "not-email",
                NORMAL.nickname,
                NORMAL.password,
                NORMAL.password,
                NORMAL.image
        );

        assertThat(validator.validate(request))
                .extracting(v -> v.getPropertyPath().toString())
                .contains(FIELD_EMAIL);
    }

    @Test
    void 회원가입_시_비밀번호는_8자_이상_20자_이하여야_하고_대소문자_숫자_특수문자를_포함해야_한다() {
        assertThat(validator.validate(joinRequest(NORMAL.email, NORMAL.nickname, "Short1!", "Short1!", NORMAL.image))).isNotEmpty();
        assertThat(validator.validate(joinRequest(NORMAL.email, NORMAL.nickname, "aaaaaaaaA1!aaaaaaaaaa", "aaaaaaaaA1!aaaaaaaaaa", NORMAL.image))).isNotEmpty();
        assertThat(validator.validate(joinRequest(NORMAL.email, NORMAL.nickname, "password1!", "password1!", NORMAL.image))).isNotEmpty();
        assertThat(validator.validate(joinRequest(NORMAL.email, NORMAL.nickname, "PASSWORD1!", "PASSWORD1!", NORMAL.image))).isNotEmpty();
        assertThat(validator.validate(joinRequest(NORMAL.email, NORMAL.nickname, "Password!", "Password!", NORMAL.image))).isNotEmpty();
        assertThat(validator.validate(joinRequest(NORMAL.email, NORMAL.nickname, "Password1", "Password1", NORMAL.image))).isNotEmpty();

        assertThat(validator.validate(NORMAL.joinRequest())).isEmpty();
    }

    @Test
    void 회원가입_시_닉네임은_공백이_없고_2자_이상_10자_이하여야_한다() {
        assertThat(validator.validate(joinRequest(NORMAL.email, "a", NORMAL.password, NORMAL.password, NORMAL.image))).isNotEmpty();
        assertThat(validator.validate(joinRequest(NORMAL.email, "abcdefghijk", NORMAL.password, NORMAL.password, NORMAL.image))).isNotEmpty();
        assertThat(validator.validate(joinRequest(NORMAL.email, "test user", NORMAL.password, NORMAL.password, NORMAL.image))).isNotEmpty();

        assertThat(validator.validate(NORMAL.joinRequest())).isEmpty();
    }

    @Test
    void 회원가입_시_이메일은_중복될_수_없다() {
        JoinRequestDTO request = NORMAL.joinRequest();

        when(userRepository.existsByEmail(NORMAL.email)).thenReturn(true);

        assertThatThrownBy(() -> userService.joinProcess(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(DUPLICATED_EMAIL_MESSAGE);
    }

    @Test
    void 회원가입_시_닉네임은_중복될_수_없다() {
        JoinRequestDTO request = NORMAL.joinRequest();

        when(userRepository.existsByEmail(NORMAL.email)).thenReturn(false);
        when(userRepository.existsByNickname(NORMAL.nickname)).thenReturn(true);

        assertThatThrownBy(() -> userService.joinProcess(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(DUPLICATED_NICKNAME_MESSAGE);
    }

    @Test
    void 회원가입_성공_시_비밀번호는_해싱되어_저장된다() {
        JoinRequestDTO request = NORMAL.joinRequest();

        when(userRepository.existsByEmail(NORMAL.email)).thenReturn(false);
        when(userRepository.existsByNickname(NORMAL.nickname)).thenReturn(false);
        when(passwordEncoder.encode(NORMAL.password)).thenReturn(NORMAL.encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", NORMAL.id);
            return saved;
        });

        userService.joinProcess(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue().getPassword()).isEqualTo(NORMAL.encodedPassword);
        assertThat(captor.getValue().getPassword()).isNotEqualTo(NORMAL.password);
    }

    @Test
    void 회원가입_성공_시_사용자_이메일_비밀번호_닉네임_이미지가_저장된다() {
        JoinRequestDTO request = NORMAL.joinRequest();

        when(userRepository.existsByEmail(NORMAL.email)).thenReturn(false);
        when(userRepository.existsByNickname(NORMAL.nickname)).thenReturn(false);
        when(passwordEncoder.encode(NORMAL.password)).thenReturn(NORMAL.encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.joinProcess(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue().getEmail()).isEqualTo(NORMAL.email);
        assertThat(captor.getValue().getPassword()).isEqualTo(NORMAL.encodedPassword);
        assertThat(captor.getValue().getNickname()).isEqualTo(NORMAL.nickname);
        assertThat(captor.getValue().getImage()).isNotNull();
    }

    @Test
    void 사용자_이미지를_입력하지_않고_회원가입하면_기본_이미지가_저장된다() {
        JoinRequestDTO request = NO_IMAGE.joinRequest();

        when(userRepository.existsByEmail(NO_IMAGE.email)).thenReturn(false);
        when(userRepository.existsByNickname(NO_IMAGE.nickname)).thenReturn(false);
        when(passwordEncoder.encode(NO_IMAGE.password)).thenReturn(NO_IMAGE.encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.joinProcess(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue().getImage()).isNotBlank();
    }

    @Test
    void 회원정보_수정_페이지에서는_현재_사용자의_이메일_닉네임_이미지를_조회할_수_있다() {
        User user = NORMAL.toUser();

        when(userRepository.findById(NORMAL.id)).thenReturn(Optional.of(user));

        Map<String, Object> result = userService.getUserInfo(NORMAL.id);

        assertThat(result.get(RESPONSE_EMAIL)).isEqualTo(NORMAL.email);
        assertThat(result.get(RESPONSE_NICKNAME)).isEqualTo(NORMAL.nickname);
        assertThat(result.get(RESPONSE_IMAGE)).isNotNull();
    }

    @Test
    void 회원정보_수정_시_닉네임과_이미지를_변경할_수_있다() {
        User user = BEFORE_UPDATE.toUser();

        when(userRepository.findById(BEFORE_UPDATE.id)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(AFTER_UPDATE.nickname)).thenReturn(false);

        Map<String, Object> result = userService.updateProfileProcess(
                AFTER_UPDATE.nickname,
                AFTER_UPDATE.image,
                BEFORE_UPDATE.id
        );

        assertThat(result.get(RESPONSE_NICKNAME)).isEqualTo(AFTER_UPDATE.nickname);
        assertThat(result.get(RESPONSE_IMAGE)).isEqualTo(AFTER_UPDATE.image);
        assertThat(user.getNickname()).isEqualTo(AFTER_UPDATE.nickname);
        assertThat(user.getImage()).isEqualTo(AFTER_UPDATE.image);
    }

    @Test
    void 회원정보_수정_시_닉네임은_회원가입과_같은_규칙을_따른다() {
        UpdateProfileRequestDTO request = updateProfileRequest("bad nick", AFTER_UPDATE.image);

        assertThat(validator.validate(request))
                .extracting(v -> v.getPropertyPath().toString())
                .contains(FIELD_NEW_NICKNAME);
    }

    @Test
    void 회원정보_수정_시_변경하려는_닉네임이_이미_사용_중이면_실패한다() {
        User user = BEFORE_UPDATE.toUser();

        when(userRepository.findById(BEFORE_UPDATE.id)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(USED_NICKNAME.nickname)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfileProcess(
                USED_NICKNAME.nickname,
                AFTER_UPDATE.image,
                BEFORE_UPDATE.id
        ))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(DUPLICATED_NICKNAME_MESSAGE);

        verify(userHistoryRepository, never()).save(any());
    }

    @Test
    void 회원정보_수정_성공_시_이전_닉네임과_이전_이미지는_이력으로_보존된다() {
        User user = BEFORE_UPDATE.toUser();

        when(userRepository.findById(BEFORE_UPDATE.id)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(AFTER_UPDATE.nickname)).thenReturn(false);

        userService.updateProfileProcess(
                AFTER_UPDATE.nickname,
                AFTER_UPDATE.image,
                BEFORE_UPDATE.id
        );

        ArgumentCaptor<UserHistory> captor = ArgumentCaptor.forClass(UserHistory.class);
        verify(userHistoryRepository).save(captor.capture());

        assertThat(captor.getValue().getNickname()).isEqualTo(BEFORE_UPDATE.nickname);
        assertThat(captor.getValue().getImage()).isEqualTo(BEFORE_UPDATE.image);
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void 비밀번호_수정_시_새_비밀번호는_회원가입과_같은_비밀번호_규칙을_따른다() {
        UpdatePasswordDTO request = updatePasswordRequest("weak", "weak");

        assertThat(validator.validate(request))
                .extracting(v -> v.getPropertyPath().toString())
                .contains(FIELD_NEW_PASSWORD, FIELD_NEW_PASSWORD_CHECK);
    }

    @Test
    void 비밀번호_수정_성공_시_새_비밀번호는_해싱되어_저장된다() {
        User user = NORMAL.toUser();
        UpdatePasswordDTO request = AFTER_UPDATE.updatePasswordRequest();

        when(userRepository.findById(NORMAL.id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(AFTER_UPDATE.password)).thenReturn(AFTER_UPDATE.encodedPassword);

        userService.updatePasswordProcess(request, NORMAL.id);

        assertThat(user.getPassword()).isEqualTo(AFTER_UPDATE.encodedPassword);
        assertThat(user.getPassword()).isNotEqualTo(AFTER_UPDATE.password);
    }

    @Test
    void 회원_탈퇴_성공_시_리프레시_토큰을_삭제하고_사용자를_탈퇴_처리한다() {
        User user = NORMAL.toUser();

        when(userRepository.findById(NORMAL.id)).thenReturn(Optional.of(user));

        userService.withdrawProcess(NORMAL.id);

        verify(refreshTokenRepository).deleteByUserId(NORMAL.id);

        assertThat(user.getDeletedAt()).isNotNull();
        assertThat(user.getEmail()).isEqualTo(deletedEmail(NORMAL.id));
        assertThat(user.getNickname()).isEqualTo(deletedNickname(NORMAL.id));
        assertThat(user.getImage()).isNull();
    }

    @Test
    void 회원_탈퇴_시_사용자가_존재하지_않으면_실패한다() {
        when(userRepository.findById(NORMAL.id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.withdrawProcess(NORMAL.id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND_MESSAGE);

        verify(refreshTokenRepository, never()).deleteByUserId(anyLong());
    }

    @Test
    void 이미_탈퇴한_사용자는_다시_탈퇴할_수_없다() {
        User user = NORMAL.toUser();
        user.delete();

        when(userRepository.findById(NORMAL.id)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.withdrawProcess(NORMAL.id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND_MESSAGE);

        verify(refreshTokenRepository, never()).deleteByUserId(anyLong());
    }

    @Test
    void 회원가입_시_비밀번호와_비밀번호_확인이_다르면_실패한다() {
        JoinRequestDTO request = joinRequest(
                NORMAL.email,
                NORMAL.nickname,
                NORMAL.password,
                "Different1!",
                NORMAL.image
        );

        assertThatThrownBy(() -> userService.joinProcess(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("mismatch_user_password");

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).existsByNickname(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 비밀번호_수정_시_새_비밀번호와_확인이_다르면_실패한다() {
        UpdatePasswordDTO request = updatePasswordRequest(
                AFTER_UPDATE.password,
                "Different1!"
        );

        assertThatThrownBy(() -> userService.updatePasswordProcess(request, NORMAL.id))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage(MISMATCH_PASSWORD_MESSAGE);

        verify(userRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void 회원정보_수정_시_닉네임과_이미지가_모두_같으면_실패한다() {
        User user = BEFORE_UPDATE.toUser();

        when(userRepository.findById(BEFORE_UPDATE.id)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updateProfileProcess(
                BEFORE_UPDATE.nickname,
                BEFORE_UPDATE.image,
                BEFORE_UPDATE.id
        ))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage(NO_USER_UPDATE_CHANGES_MESSAGE);

        verify(userHistoryRepository, never()).save(any());
        verify(userRepository, never()).existsByNickname(anyString());
    }

    @Test
    void 회원정보_수정_시_이미지만_변경하면_닉네임_중복검사를_하지_않는다() {
        User user = BEFORE_UPDATE.toUser();

        when(userRepository.findById(BEFORE_UPDATE.id)).thenReturn(Optional.of(user));

        Map<String, Object> result = userService.updateProfileProcess(
                BEFORE_UPDATE.nickname,
                AFTER_UPDATE.image,
                BEFORE_UPDATE.id
        );

        assertThat(result.get("user_nickname")).isEqualTo(BEFORE_UPDATE.nickname);
        assertThat(result.get("user_image")).isEqualTo(AFTER_UPDATE.image);

        verify(userRepository, never()).existsByNickname(anyString());
        verify(userHistoryRepository).save(any(UserHistory.class));
    }

    private JoinRequestDTO joinRequest(
            String email,
            String nickname,
            String password,
            String passwordCheck,
            String image
    ) {
        JoinRequestDTO request = new JoinRequestDTO();
        ReflectionTestUtils.setField(request, "userEmail", email);
        ReflectionTestUtils.setField(request, "userNickname", nickname);
        ReflectionTestUtils.setField(request, "userPassword", password);
        ReflectionTestUtils.setField(request, "userPasswordCheck", passwordCheck);
        ReflectionTestUtils.setField(request, "userImage", image);
        return request;
    }

    private UpdateProfileRequestDTO updateProfileRequest(String nickname, String image) {
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO();
        ReflectionTestUtils.setField(request, "userNewNickname", nickname);
        ReflectionTestUtils.setField(request, "userNewImage", image);
        return request;
    }

    private UpdatePasswordDTO updatePasswordRequest(String password, String passwordCheck) {
        UpdatePasswordDTO request = new UpdatePasswordDTO();
        ReflectionTestUtils.setField(request, "userNewPassword", password);
        ReflectionTestUtils.setField(request, "userNewPasswordCheck", passwordCheck);
        return request;
    }

    private String deletedEmail(Long userId) {
        return "deleted_user_" + userId + "@deleted.local";
    }

    private String deletedNickname(Long userId) {
        return "deleted_user_" + userId;
    }
}