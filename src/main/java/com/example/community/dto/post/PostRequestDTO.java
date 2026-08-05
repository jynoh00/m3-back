package com.example.community.dto.post;

import com.example.community.common.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class
PostRequestDTO {
    @JsonProperty("post_title")
    @NotBlank(message = ValidationMessage.POST_TITLE_REQUIRED)
    @Size(min = 2, max = 26, message = ValidationMessage.INVALID_POST_TITLE_LENGTH)
    private String postTitle;

    @JsonProperty("post_content")
    @NotBlank(message = ValidationMessage.POST_CONTENT_REQUIRED)
    @Size(max = 100, message = ValidationMessage.INVALID_POST_CONTENT_LENGTH)
    private String postContent;

    @JsonProperty("music_title")
    @NotBlank(message = ValidationMessage.MUSIC_TITLE_REQUIRED)
    private String musicTitle;

    @JsonProperty("cover_image")
    @NotBlank(message = ValidationMessage.COVER_IMAGE_REQUIRED)
    private String coverImage;

    @JsonProperty("artist_names")
    @NotEmpty(message = ValidationMessage.ARTIST_NAMES_REQUIRED)
    private List<@NotBlank(message = ValidationMessage.ARTIST_NAME_BLANK) String> artistNames;
}