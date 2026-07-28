package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class
PostRequestDTO {
    @JsonProperty("post_title")
    @NotBlank(message = "post_title_required")
    @Size(min = 2, message = "invalid_post_title_length")
    private String postTitle;

    @JsonProperty("post_content")
    @NotBlank(message = "post_content_required")
    private String postContent;

    @JsonProperty("post_image")
    private String postImage;
}