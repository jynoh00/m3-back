package com.example.community.dto;

import com.example.community.entity.main.comment.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDTO {
    @JsonProperty("comment_id")
    private final Long commentId;

    @JsonProperty("comment_content")
    private final String commentContent;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private final LocalDateTime updatedAt;

    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("user_nickname")
    private final String userNickname;

    @JsonProperty("user_image")
    private final String userImage;

    public CommentResponseDTO(Comment comment) {
        this.commentId = comment.getId();
        this.commentContent = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.userId = comment.getUser().getId();
        this.userNickname = comment.getUser().getNickname();
        this.userImage = comment.getUser().getImage();
    }
}
