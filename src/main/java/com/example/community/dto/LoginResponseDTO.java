package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginResponseDTO {
    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("user_email")
    private final String userEmail;

    @JsonProperty("user_nickname")
    private final String userNickname;

    @JsonProperty("user_image")
    private final String userImage;

    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("refresh_token")
    private final String refreshToken;

    public LoginResponseDTO(
            Long userId,
            String userEmail,
            String userNickname,
            String userImage,
            String accessToken,
            String refreshToken
    ) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userImage = userImage;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
