package com.example.community.dto;

import lombok.Getter;

@Getter
public class LikeResponseDTO {
    private final Long likeCount;
    private final boolean isLike;

    public LikeResponseDTO(Long likeCount, boolean isLike) {
        this.likeCount = likeCount;
        this.isLike = isLike;
    }
}
