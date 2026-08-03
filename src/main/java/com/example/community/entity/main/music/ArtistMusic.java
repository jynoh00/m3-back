package com.example.community.entity.main.music;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "artist_music")
@Getter
public class ArtistMusic {
    @EmbeddedId
    private ArtistMusicId id;

    @MapsId("artistId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @MapsId("musicId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    protected ArtistMusic() {
    }

    public ArtistMusic(Artist artist, Music music) {
        this.id = new ArtistMusicId(artist.getId(), music.getId());
        this.artist = artist;
        this.music = music;
    }
}
