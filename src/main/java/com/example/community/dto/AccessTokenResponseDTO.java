package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AccessTokenResponseDTO {
    @JsonProperty("access_token")
    private final String accessToken;

    public AccessTokenResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
