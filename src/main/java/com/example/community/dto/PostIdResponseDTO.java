package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PostIdResponseDTO {
    @JsonProperty("post_id")
    private final Long postId;

    public PostIdResponseDTO(Long postId) {
        this.postId = postId;
    }
}
