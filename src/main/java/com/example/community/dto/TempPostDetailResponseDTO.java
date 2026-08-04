package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TempPostDetailResponseDTO {
    @JsonProperty("temp_post_id")
    private final Long tempPostId;

    @JsonProperty("post_title")
    private final String postTitle;

    @JsonProperty("post_content")
    private final String postContent;

    @JsonProperty("music")
    private final MusicSearchResultDTO music;

    public TempPostDetailResponseDTO(Long tempPostId, String postTitle, String postContent, MusicSearchResultDTO music) {
        this.tempPostId = tempPostId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.music = music;
    }
}
