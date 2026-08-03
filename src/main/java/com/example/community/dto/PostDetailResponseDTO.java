package com.example.community.dto;

import com.example.community.entity.main.music.ArtistMusic;
import com.example.community.entity.main.post.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostDetailResponseDTO {
    @JsonProperty("post_id")
    private final Long postId;

    @JsonProperty("post_title")
    private final String postTitle;

    @JsonProperty("post_content")
    private final String postContent;

    @JsonProperty("music")
    private final MusicSearchResultDTO music;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private final LocalDateTime updatedAt;

    @JsonProperty("like_count")
    private final Long likeCount;

    @JsonProperty("is_liked")
    private final boolean liked;

    @JsonProperty("view_count")
    private final Long viewCount;

    @JsonProperty("report_count")
    private final Integer reportCount;

    @JsonProperty("blinded_at")
    private final LocalDateTime blindedAt;

    @JsonProperty("comment_count")
    private final Long commentCount;

    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("user_nickname")
    private final String userNickname;

    @JsonProperty("user_image")
    private final String userImage;

    public PostDetailResponseDTO(Post post, List<ArtistMusic> artistMusicsOfMusic, boolean isLiked) {
        this.postId = post.getId();
        this.postTitle = post.getTitle();
        this.postContent = post.getContent();
        this.music = new MusicSearchResultDTO(post.getArtistMusic().getMusic(), artistMusicsOfMusic);
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likeCount = post.getLikeCount();
        this.liked = isLiked;
        this.viewCount = post.getViewCount();
        this.reportCount = post.getReportCount();
        this.blindedAt = post.getBlindedAt();
        this.commentCount = post.getCommentCount();
        this.userId = post.getUser().getId();
        this.userNickname = post.getUser().getNickname();
        this.userImage = post.getUser().getImage();
    }
}
