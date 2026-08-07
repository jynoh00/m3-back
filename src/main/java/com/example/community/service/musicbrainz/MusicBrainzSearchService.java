package com.example.community.service.musicbrainz;

import com.example.community.dto.music.MusicSearchResultDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MusicBrainzSearchService {
    private static final String SEARCH_ENDPOINT = "https://musicbrainz.org/ws/2/recording";
    private static final String COVER_ART_URL_TEMPLATE = "https://coverartarchive.org/release-group/%s/front-500";
    private static final String USER_AGENT = "community-music-search/1.0 ( non-commercial community project )";
    private static final int SEARCH_LIMIT = 20;

    private final RestTemplate restTemplate;

    public List<MusicSearchResultDTO> search(String keyword) {
        URI requestUri = UriComponentsBuilder.fromUriString(SEARCH_ENDPOINT)
                .queryParam("query", keyword)
                .queryParam("fmt", "json")
                .queryParam("limit", SEARCH_LIMIT)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);

        MusicBrainzSearchResponse response;

        try {
            response = restTemplate.exchange(requestUri, HttpMethod.GET, new HttpEntity<>(headers), MusicBrainzSearchResponse.class)
                    .getBody();
        } catch (RestClientException e) {
            return List.of();
        }

        if (response == null || response.recordings() == null) {
            return List.of();
        }

        return response.recordings().stream()
                .filter(recording -> recording.title() != null && !recording.title().isBlank())
                .map(this::toSearchResult)
                .toList();
    }

    private MusicSearchResultDTO toSearchResult(Recording recording) {
        return new MusicSearchResultDTO(recording.title(), extractCoverImage(recording), extractArtistNames(recording));
    }

    private List<String> extractArtistNames(Recording recording) {
        if (recording.artistCredit() == null) {
            return List.of();
        }

        return recording.artistCredit().stream()
                .map(ArtistCredit::name)
                .filter(name -> name != null && !name.isBlank())
                .toList();
    }

    private String extractCoverImage(Recording recording) {
        if (recording.releases() == null) {
            return null;
        }

        return recording.releases().stream()
                .map(Release::releaseGroup)
                .filter(Objects::nonNull)
                .map(ReleaseGroup::id)
                .filter(Objects::nonNull)
                .findFirst()
                .map(releaseGroupId -> String.format(COVER_ART_URL_TEMPLATE, releaseGroupId))
                .orElse(null);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MusicBrainzSearchResponse(@JsonProperty("recordings") List<Recording> recordings) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Recording(
            @JsonProperty("title") String title,
            @JsonProperty("artist-credit") List<ArtistCredit> artistCredit,
            @JsonProperty("releases") List<Release> releases
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArtistCredit(
            @JsonProperty("name") String name
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Release(
            @JsonProperty("release-group") ReleaseGroup releaseGroup
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ReleaseGroup(
            @JsonProperty("id") String id
    ) {
    }
}
