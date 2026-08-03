package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TempPostIdResponseDTO {
    @JsonProperty("temp_post_id")
    private final Long tempPostId;

    public TempPostIdResponseDTO(Long tempPostId) {
        this.tempPostId = tempPostId;
    }
}
