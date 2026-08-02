package com.example.community.dto;

import com.example.community.entity.main.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserInfoResponseDTO {
    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("user_email")
    private final String userEmail;

    @JsonProperty("user_nickname")
    private final String userNickname;

    @JsonProperty("user_image")
    private final String userImage;

    public UserInfoResponseDTO(User user) {
        this.userId = user.getId();
        this.userEmail = user.getEmail();
        this.userNickname = user.getNickname();
        this.userImage = user.getImage();
    }
}
