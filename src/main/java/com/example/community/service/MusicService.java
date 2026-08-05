package com.example.community.service;

import com.example.community.dto.music.MusicSearchResponseDTO;
import com.example.community.dto.music.MusicSearchResultDTO;
import com.example.community.entity.main.music.Music;
import com.example.community.repository.main.music.MusicRepository;
import com.example.community.service.support.MusicFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicService {
    private final MusicRepository musicRepository;
    private final MusicFinder musicFinder;

    @Transactional(readOnly = true)
    public MusicSearchResponseDTO searchMusicProcess(String keyword) {
        return new MusicSearchResponseDTO(
                keyword,
                musicRepository.searchByKeyword(keyword).stream()
                        .map(this::toSearchResult)
                        .toList()
        );
    }

    private MusicSearchResultDTO toSearchResult(Music music) {
        return new MusicSearchResultDTO(music, musicFinder.getArtistMusicsOf(music));
    }
}
