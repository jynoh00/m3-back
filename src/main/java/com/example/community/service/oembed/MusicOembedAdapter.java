package com.example.community.service.oembed;

import com.example.community.dto.music.OembedResultDTO;

public interface MusicOembedAdapter {
    boolean supports(String url);

    OembedResultDTO fetch(String url);
}