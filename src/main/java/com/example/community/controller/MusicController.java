package com.example.community.controller;

import com.example.community.common.ExceptionMessage;
import com.example.community.common.ResponseFormat;
import com.example.community.common.ResponseMessage;
import com.example.community.dto.music.MusicSearchResponseDTO;
import com.example.community.dto.music.OembedRequestDTO;
import com.example.community.dto.music.OembedResultDTO;
import com.example.community.exception.InvalidRequestException;
import com.example.community.service.MusicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/musics")
public class MusicController {
    private final MusicService musicService;

    @GetMapping("/search")
    public ResponseEntity<?> searchMusic(@RequestParam String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new InvalidRequestException(ExceptionMessage.SEARCH_KEYWORD_REQUIRED.getMessage());
        }

        MusicSearchResponseDTO response = musicService.searchMusicProcess(keyword.trim());

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.MUSIC_SEARCH_SUCCESS.getMessage(), response));
    }

    @PostMapping("/oembed")
    public ResponseEntity<?> fetchOembed(@Valid @RequestBody OembedRequestDTO requestDTO) {
        OembedResultDTO response = musicService.fetchOembedProcess(requestDTO);

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.OEMBED_FETCH_SUCCESS.getMessage(), response));
    }
}
