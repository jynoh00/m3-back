package com.example.community.dto;

import com.example.community.common.RedirectPath;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RedirectResponseDTO {
    @JsonProperty("redirect_url")
    private final String redirectUrl;

    public RedirectResponseDTO(RedirectPath redirectPath) {
        this.redirectUrl = redirectPath.getPath();
    }
}