package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TempPostDetailResponseDTO {
    @JsonProperty("post_title")
    private final String postTitle;

    @JsonProperty("post_content")
    private final String postContent;

    @JsonProperty("music")
    private final MusicSearchResultDTO music;

    public TempPostDetailResponseDTO(String postTitle, String postContent, MusicSearchResultDTO music) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.music = music;
    }
}
