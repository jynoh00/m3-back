package com.example.community.dto;

import com.example.community.entity.main.comment.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class CommentPageResponseDTO {
    @JsonProperty("comments")
    private final List<CommentResponseDTO> comments;

    @JsonProperty("page")
    private final int page;

    @JsonProperty("comments_count")
    private final int commentsCount;

    @JsonProperty("total_pages")
    private final int totalPages;

    @JsonProperty("total_count")
    private final long totalCount;

    private CommentPageResponseDTO(
            List<CommentResponseDTO> comments, int page, int commentsCount, int totalPages, long totalCount) {
        this.comments = comments;
        this.page = page;
        this.commentsCount = commentsCount;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
    }

    public static CommentPageResponseDTO of(Page<Comment> commentPage, int page) {
        List<CommentResponseDTO> comments = commentPage.getContent().stream()
                .map(CommentResponseDTO::new)
                .toList();

        return new CommentPageResponseDTO(
                comments, page, comments.size(), commentPage.getTotalPages(), commentPage.getTotalElements());
    }
}