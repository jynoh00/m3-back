package com.example.community.dto;

import com.example.community.common.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class
PostRequestDTO {
    @JsonProperty("post_title")
    @NotBlank(message = ValidationMessage.POST_TITLE_REQUIRED)
    @Size(min = 2, message = ValidationMessage.INVALID_POST_TITLE_LENGTH)
    private String postTitle;

    @JsonProperty("post_content")
    @NotBlank(message = ValidationMessage.POST_CONTENT_REQUIRED)
    private String postContent;

    @JsonProperty("post_image")
    private String postImage;
}