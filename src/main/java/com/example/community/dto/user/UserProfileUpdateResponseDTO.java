package com.example.community.dto.user;

import com.example.community.entity.main.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserProfileUpdateResponseDTO {
    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("user_nickname")
    private final String userNickname;

    @JsonProperty("user_image")
    private final String userImage;

    public UserProfileUpdateResponseDTO(User user) {
        this.userId = user.getId();
        this.userNickname = user.getNickname();
        this.userImage = user.getImage();
    }
}
