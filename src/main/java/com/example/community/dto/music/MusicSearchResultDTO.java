package com.example.community.dto.music;

import com.example.community.entity.main.music.ArtistMusic;
import com.example.community.entity.main.music.Music;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MusicSearchResultDTO {
    @JsonProperty("music_id")
    private final Long musicId;

    @JsonProperty("music_title")
    private final String musicTitle;

    @JsonProperty("cover_image")
    private final String coverImage;

    @JsonProperty("artists")
    private final List<ArtistSummaryDTO> artists;

    public MusicSearchResultDTO(Music music, List<ArtistMusic> artistMusics) {
        this.musicId = music.getId();
        this.musicTitle = music.getTitle();
        this.coverImage = music.getCoverImage();
        this.artists = artistMusics.stream()
                .map(am -> new ArtistSummaryDTO(am.getArtist()))
                .toList();
    }

    public MusicSearchResultDTO(String musicTitle, String coverImage, List<String> artistNames) {
        this.musicId = null;
        this.musicTitle = musicTitle;
        this.coverImage = coverImage;
        this.artists = artistNames.stream()
                .map(ArtistSummaryDTO::new)
                .toList();
    }
}
