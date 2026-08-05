package com.example.community.dto.post;

import com.example.community.entity.main.post.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSummaryDTO {
    @JsonProperty("post_id")
    private final Long postId;

    @JsonProperty("post_title")
    private final String postTitle;

    @JsonProperty("post_content")
    private final String postContent;

    @JsonProperty("music_title")
    private final String musicTitle;

    @JsonProperty("cover_image")
    private final String coverImage;

    @JsonProperty("artist_name")
    private final String artistName;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("like_count")
    private final Long likeCount;

    @JsonProperty("view_count")
    private final Long viewCount;

    @JsonProperty("report_count")
    private final Integer reportCount;

    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("user_nickname")
    private final String userNickname;

    @JsonProperty("user_image")
    private final String userImage;

    @JsonProperty("comment_count")
    private final Long commentCount;

    public PostSummaryDTO(Post post) {
        this.postId = post.getId();
        this.postTitle = post.getTitle();
        this.postContent = post.getContent();
        this.musicTitle = post.getArtistMusic().getMusic().getTitle();
        this.coverImage = post.getArtistMusic().getMusic().getCoverImage();
        this.artistName = post.getArtistMusic().getArtist().getName();
        this.createdAt = post.getCreatedAt();
        this.likeCount = post.getLikeCount();
        this.viewCount = post.getViewCount();
        this.reportCount = post.getReportCount();
        this.userId = post.getUser().getId();
        this.userNickname = post.getUser().getNickname();
        this.userImage = post.getUser().getImage();
        this.commentCount = post.getCommentCount();
    }
}