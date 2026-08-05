package com.example.community.dto.music;

import com.example.community.entity.main.music.Artist;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ArtistSummaryDTO {
    @JsonProperty("artist_id")
    private final Long artistId;

    @JsonProperty("artist_name")
    private final String artistName;

    public ArtistSummaryDTO(Artist artist) {
        this.artistId = artist.getId();
        this.artistName = artist.getName();
    }
}
