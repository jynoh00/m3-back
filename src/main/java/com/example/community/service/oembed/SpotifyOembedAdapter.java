package com.example.community.service.oembed;

import com.example.community.common.ExceptionMessage;
import com.example.community.dto.music.OembedResultDTO;
import com.example.community.exception.InvalidRequestException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SpotifyOembedAdapter implements MusicOembedAdapter {
    private static final String SUPPORTED_HOST = "open.spotify.com";
    private static final String OEMBED_ENDPOINT = "https://open.spotify.com/oembed";
    private static final String PROVIDER = "spotify";

    private final RestTemplate restTemplate;

    @Override
    public boolean supports(String url) {
        return SUPPORTED_HOST.equals(OembedUrlHost.extract(url));
    }

    @Override
    public OembedResultDTO fetch(String url) {
        URI requestUri = UriComponentsBuilder.fromUriString(OEMBED_ENDPOINT)
                .queryParam("url", url)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        SpotifyOembedResponse response;

        try {
            response = restTemplate.getForObject(requestUri, SpotifyOembedResponse.class);
        } catch (RestClientException e) {
            throw new InvalidRequestException(ExceptionMessage.OEMBED_FETCH_FAILED.getMessage());
        }

        if (response == null || response.title() == null) {
            throw new InvalidRequestException(ExceptionMessage.OEMBED_FETCH_FAILED.getMessage());
        }

        // 아티스트명 제공안함(스포티파이 oEmbed) => 일단 빈 리스트
        return new OembedResultDTO(PROVIDER, response.title(), response.thumbnailUrl(), List.of());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SpotifyOembedResponse(
            @JsonProperty("title") String title,
            @JsonProperty("thumbnail_url") String thumbnailUrl
    ) {
    }
}