package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.dto.music.MusicSearchResponseDTO;
import com.example.community.dto.music.MusicSearchResultDTO;
import com.example.community.dto.music.OembedRequestDTO;
import com.example.community.dto.music.OembedResultDTO;
import com.example.community.entity.main.music.Music;
import com.example.community.exception.InvalidRequestException;
import com.example.community.repository.main.music.MusicRepository;
import com.example.community.service.musicbrainz.MusicBrainzSearchService;
import com.example.community.service.oembed.MusicOembedAdapter;
import com.example.community.service.support.MusicFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicService {
    private final MusicRepository musicRepository;
    private final MusicFinder musicFinder;
    private final List<MusicOembedAdapter> oembedAdapters;
    private final MusicBrainzSearchService musicBrainzSearchService;

    @Transactional(readOnly = true)
    public MusicSearchResponseDTO searchMusicProcess(String keyword) {
        List<MusicSearchResultDTO> internalResults = musicRepository.searchByKeyword(keyword).stream()
                .map(this::toSearchResult)
                .toList();

        if (!internalResults.isEmpty()) {
            return new MusicSearchResponseDTO(keyword, internalResults);
        }

        return new MusicSearchResponseDTO(keyword, musicBrainzSearchService.search(keyword));
    }

    @Transactional
    public OembedResultDTO fetchOembedProcess(OembedRequestDTO requestDTO) {
        String url = requestDTO.getUrl().trim();

        return oembedAdapters.stream()
                .filter(adapter -> adapter.supports(url))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException(ExceptionMessage.UNSUPPORTED_MUSIC_PROVIDER.getMessage()))
                .fetch(url);
    }

    private MusicSearchResultDTO toSearchResult(Music music) {
        return new MusicSearchResultDTO(music, musicFinder.getArtistMusicsOf(music));
    }
}
