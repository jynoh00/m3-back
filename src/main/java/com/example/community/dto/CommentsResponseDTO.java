package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentsResponseDTO {
    @JsonProperty("comments")
    private final List<CommentResponseDTO> comments;

    public CommentsResponseDTO(List<CommentResponseDTO> comments) {
        this.comments = comments;
    }
}
