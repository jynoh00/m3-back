package com.example.community.service.support;

import com.example.community.common.ExceptionMessage;
import com.example.community.entity.main.user.User;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.main.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserRepository userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
    }
}
