package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LikeResponseDTO {
    @JsonProperty("like_count")
    private final Long likeCount;

    @JsonProperty("increase_like_count")
    private final boolean isLike;

    public LikeResponseDTO(Long likeCount, boolean isLike) {
        this.likeCount = likeCount;
        this.isLike = isLike;
    }
}
