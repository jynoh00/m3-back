package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.dto.JoinRequestDTO;
import com.example.community.dto.UpdatePasswordDTO;
import com.example.community.entity.history.user.UserHistory;
import com.example.community.entity.main.user.User;
import com.example.community.exception.DuplicateResourceException;
import com.example.community.exception.InvalidRequestException;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.history.user.UserHistoryRepository;
import com.example.community.repository.main.auth.RefreshTokenRepository;
import com.example.community.repository.main.user.UserRepository;
import com.example.community.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHistoryRepository userHistoryRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public Long joinProcess(JoinRequestDTO joinRequestDTO) {
        passwordCheck(
                joinRequestDTO.getUserPassword(),
                joinRequestDTO.getUserPasswordCheck()
        );

        duplicatedCheck(
                joinRequestDTO.getUserEmail(),
                joinRequestDTO.getUserNickname()
        );

        User user = new User(
                joinRequestDTO.getUserEmail(),
                passwordEncoding(joinRequestDTO.getUserPassword()),
                joinRequestDTO.getUserNickname(),
                joinRequestDTO.getUserImage()
        );

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", user.getId());
        response.put("user_email", user.getEmail());
        response.put("user_nickname", user.getNickname());
        response.put("user_image", user.getImage());

        return response;
    }

    @Transactional
    public Map<String, Object> updateProfileProcess(String userNewNickname, String userNewImage, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        validateUserProfileChange(user, userNewNickname, userNewImage);

        UserHistory userHistory = new UserHistory(
                user,
                user.getNickname(),
                user.getImage()
        );

        userHistoryRepository.save(userHistory);

        user.updateProfile(userNewNickname, userNewImage);

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", user.getId());
        response.put("user_nickname", user.getNickname());
        response.put("user_image", user.getImage());

        return response;
    }

    @Transactional
    public void updatePasswordProcess(UpdatePasswordDTO updatePasswordDTO, Long userId) {
        String userNewPassword = updatePasswordDTO.getUserNewPassword();
        String userNewPasswordCheck = updatePasswordDTO.getUserNewPasswordCheck();

        passwordCheck(userNewPassword, userNewPasswordCheck);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        user.updatePassword(passwordEncoding(userNewPassword));
    }

    @Transactional
    public void withdrawProcess(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        if (user.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage());
        }

        refreshTokenRepository.deleteByUserId(userId);

        user.delete();
    }

    private void duplicatedCheck(String userEmail, String userNickname) {
        duplicatedCheckEmail(userEmail);
        duplicatedCheckNickname(userNickname);
    }

    private void duplicatedCheckEmail(String userEmail) {
        if (userRepository.existsByEmail(userEmail)) {
            throw new DuplicateResourceException(ExceptionMessage.DUPLICATED_USER_EMAIL.getMessage());
        }
    }

    private void duplicatedCheckNickname(String userNickname) {
        if (userRepository.existsByNickname(userNickname)) {
            throw new DuplicateResourceException(ExceptionMessage.DUPLICATED_USER_NICKNAME.getMessage());
        }
    }

    private void passwordCheck(String userPassword, String userPasswordCheck) {
        if (!userPassword.equals(userPasswordCheck)) {
            throw new InvalidRequestException(ExceptionMessage.MISMATCH_USER_PASSWORD.getMessage());
        }
    }

    private String passwordEncoding(String userPassword) {
        return passwordEncoder.encode(userPassword);
    }

    private void validateUserProfileChange(User user, String userNewNickname, String userNewImage) {
        String beforeNickname = user.getNickname();
        String beforeImage = user.getImage();

        boolean nicknameChanged = !Objects.equals(userNewNickname, beforeNickname);
        boolean imageChanged = !Objects.equals(userNewImage, beforeImage);

        if (!nicknameChanged && !imageChanged) {
            throw new InvalidRequestException(ExceptionMessage.NO_USER_UPDATE_CHANGES.getMessage());
        }

        if (nicknameChanged) {
            duplicatedCheckNickname(userNewNickname);
        }
    }
}