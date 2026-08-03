package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CommentIdResponseDTO {
    @JsonProperty("comment_id")
    private final Long commentId;

    public CommentIdResponseDTO(Long commentId) {
        this.commentId = commentId;
    }
}