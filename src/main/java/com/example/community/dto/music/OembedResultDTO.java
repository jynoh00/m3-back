package com.example.community.dto.music;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class OembedResultDTO {
    @JsonProperty("provider")
    private final String provider;

    @JsonProperty("music_title")
    private final String musicTitle;

    @JsonProperty("cover_image")
    private final String coverImage;

    @JsonProperty("artist_names")
    private final List<String> artistNames;

    public OembedResultDTO(String provider, String musicTitle, String coverImage, List<String> artistNames) {
        this.provider = provider;
        this.musicTitle = musicTitle;
        this.coverImage = coverImage;
        this.artistNames = artistNames;
    }
}