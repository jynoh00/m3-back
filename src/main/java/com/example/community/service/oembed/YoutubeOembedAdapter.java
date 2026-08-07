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
import java.util.Set;

@Component
@RequiredArgsConstructor
public class YoutubeOembedAdapter implements MusicOembedAdapter {
    private static final Set<String> SUPPORTED_HOSTS = Set.of("youtube.com", "www.youtube.com", "music.youtube.com", "youtu.be");
    private static final String OEMBED_ENDPOINT = "https://www.youtube.com/oembed";
    private static final String PROVIDER = "youtube";

    private final RestTemplate restTemplate;


    @Override
    public boolean supports(String url) {
        return SUPPORTED_HOSTS.contains(OembedUrlHost.extract(url));
    }

    @Override
    public OembedResultDTO fetch(String url) {
        URI requestUri = UriComponentsBuilder.fromUriString(OEMBED_ENDPOINT)
                .queryParam("url", url)
                .queryParam("format", "json")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        YoutubeOembedResponse response;

        try {
            response = restTemplate.getForObject(requestUri, YoutubeOembedResponse.class);
        } catch (RestClientException e) {
            throw new InvalidRequestException(ExceptionMessage.OEMBED_FETCH_FAILED.getMessage());
        }

        if (response == null || response.title() == null) {
            throw new InvalidRequestException(ExceptionMessage.OEMBED_FETCH_FAILED.getMessage());
        }

        List<String> artistNames = (response.authorName() == null || response.authorName().isBlank())
                ? List.of()
                : List.of(response.authorName());

        return new OembedResultDTO(PROVIDER, response.title(), response.thumbnailUrl(), artistNames);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record YoutubeOembedResponse(
            @JsonProperty("title") String title,
            @JsonProperty("author_name") String authorName,
            @JsonProperty("thumbnail_url") String thumbnailUrl
    ) {
    }
}
