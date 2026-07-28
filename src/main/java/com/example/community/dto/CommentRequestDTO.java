package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequestDTO {
    @JsonProperty("comment_content")
    @NotBlank(message = "comment_content_required")
    private String commentContent;

    @JsonProperty("parent_comment_id")
    @Nullable
    private Long parentCommentId = null;
}
