package com.example.community.service.support;

import com.example.community.common.ExceptionMessage;
import com.example.community.entity.main.music.Artist;
import com.example.community.entity.main.music.ArtistMusic;
import com.example.community.entity.main.music.Music;
import com.example.community.exception.InvalidRequestException;
import com.example.community.repository.main.music.ArtistMusicRepository;
import com.example.community.repository.main.music.ArtistRepository;
import com.example.community.repository.main.music.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MusicFinder {
    private final ArtistRepository artistRepository;
    private final MusicRepository musicRepository;
    private final ArtistMusicRepository artistMusicRepository;

    public ArtistMusic resolveArtistMusic(String musicTitle, String coverImage, List<String> artistNames) {
        if (musicTitle == null || musicTitle.isBlank()) {
            return null;
        }

        Music music = musicRepository.findByTitle(musicTitle)
                .orElseGet(() -> musicRepository.save(new Music(musicTitle, coverImage)));

        ArtistMusic representative = null;

        for (String rawName : artistNames == null ? List.<String>of() : artistNames) {
            String artistName = rawName == null ? "" : rawName.trim();

            if (artistName.isBlank()) {
                continue;
            }

            Artist artist = artistRepository.findByName(artistName)
                    .orElseGet(() -> artistRepository.save(new Artist(artistName)));

            ArtistMusic artistMusic = artistMusicRepository.findByArtistIdAndMusicId(artist.getId(), music.getId())
                    .orElseGet(() -> artistMusicRepository.save(new ArtistMusic(artist, music)));

            if (representative == null) {
                representative = artistMusic;
            }
        }

        if (representative == null) {
            throw new InvalidRequestException(ExceptionMessage.MUSIC_NOT_SELECTED.getMessage());
        }

        return representative;
    }

    public List<ArtistMusic> getArtistMusicsOf(Music music) {
        return artistMusicRepository.findByMusicIdWithArtist(music.getId());
    }
}

