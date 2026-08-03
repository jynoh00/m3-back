package com.example.community.entity.main.music;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
public class ArtistMusicId implements Serializable {

    @Column(name = "artist_id")
    private Long artistId;

    @Column(name = "music_id")
    private Long musicId;

    protected ArtistMusicId() {
    }

    public ArtistMusicId(Long artistId, Long musicId) {
        this.artistId = artistId;
        this.musicId = musicId;
    }
}