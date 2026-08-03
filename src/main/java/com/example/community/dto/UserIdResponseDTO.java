package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserIdResponseDTO {
    @JsonProperty("user_id")
    private final Long userId;

    public UserIdResponseDTO(Long userId) {
        this.userId = userId;
    }
}
