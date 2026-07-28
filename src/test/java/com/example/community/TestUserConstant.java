package com.example.community;

import com.example.community.dto.JoinRequestDTO;
import com.example.community.dto.UpdatePasswordDTO;
import com.example.community.entity.main.user.User;
import org.springframework.test.util.ReflectionTestUtils;

public enum TestUserConstant {
    NORMAL(
            1L,
            "test@test.com",
            "tester",
            "Password1!",
            "hashed-password",
            "normal.png"
    ),
    NO_IMAGE(
            2L,
            "no-image@test.com",
            "noImage",
            "Password1!",
            "hashed-password",
            null
    ),
    BEFORE_UPDATE(
            3L,
            "before@test.com",
            "oldNick",
            "Password1!",
            "old-hashed-password",
            "old.png"
    ),
    AFTER_UPDATE(
            3L,
            "before@test.com",
            "newNick",
            "Newpass1!",
            "new-hashed-password",
            "new.png"
    ),
    USED_NICKNAME(
            4L,
            "used@test.com",
            "usedNick",
            "Password1!",
            "hashed-password",
            "used.png"
    );

    public final Long id;
    public final String email;
    public final String nickname;
    public final String password;
    public final String encodedPassword;
    public final String image;

    TestUserConstant(
            Long id,
            String email,
            String nickname,
            String password,
            String encodedPassword,
            String image
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.encodedPassword = encodedPassword;
        this.image = image;
    }

    public JoinRequestDTO joinRequest() {
        JoinRequestDTO request = new JoinRequestDTO();
        ReflectionTestUtils.setField(request, "userEmail", email);
        ReflectionTestUtils.setField(request, "userNickname", nickname);
        ReflectionTestUtils.setField(request, "userPassword", password);
        ReflectionTestUtils.setField(request, "userPasswordCheck", password);
        ReflectionTestUtils.setField(request, "userImage", image);
        return request;
    }

    public UpdatePasswordDTO updatePasswordRequest() {
        UpdatePasswordDTO request = new UpdatePasswordDTO();
        ReflectionTestUtils.setField(request, "userNewPassword", password);
        ReflectionTestUtils.setField(request, "userNewPasswordCheck", password);
        return request;
    }

    public User toUser() {
        User user = new User(email, encodedPassword, nickname, image);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public User toDeletedUser() {
        User user = toUser();
        user.delete();
        return user;
    }
}