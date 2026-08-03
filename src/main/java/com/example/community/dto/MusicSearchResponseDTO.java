package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MusicSearchResponseDTO {
    @JsonProperty("keyword")
    private final String keyword;

    @JsonProperty("results")
    private final List<MusicSearchResultDTO> results;

    public MusicSearchResponseDTO(String keyword, List<MusicSearchResultDTO> results) {
        this.keyword = keyword;
        this.results = results;
    }
}