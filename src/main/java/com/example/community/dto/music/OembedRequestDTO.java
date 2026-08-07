package com.example.community.dto.music;

import com.example.community.common.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OembedRequestDTO {
    @JsonProperty("url")
    @NotBlank(message = ValidationMessage.MUSIC_URL_REQUIRED)
    private String url;
}