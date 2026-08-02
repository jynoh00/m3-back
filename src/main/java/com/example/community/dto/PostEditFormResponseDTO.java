package com.example.community.dto;

import com.example.community.common.PostGuideMessage;
import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.post.PostContent;
import com.example.community.entity.main.post.TempPost;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PostEditFormResponseDTO {
    @JsonProperty("post_title")
    private final String postTitle;

    @JsonProperty("post_content")
    private final String postContent;

    @JsonProperty("post_image")
    private final String postImage;

    @JsonProperty("has_temp_post")
    private final boolean hasTempPost;

    @JsonProperty("temp_post_id")
    private final Long tempPostId;

    @JsonProperty("temp_message")
    private final String tempMessage;

    private PostEditFormResponseDTO(
            String postTitle, String postContent, String postImage,
            boolean hasTempPost, Long tempPostId, String tempMessage) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postImage = postImage;
        this.hasTempPost = hasTempPost;
        this.tempPostId = tempPostId;
        this.tempMessage = tempMessage;
    }

    public static PostEditFormResponseDTO of(Post post, PostContent postContent, TempPost tempPost) {
        if (tempPost == null) {
            return new PostEditFormResponseDTO(
                    post.getTitle(), postContent.getContent(), post.getImage(), false, null, null);
        }

        return new PostEditFormResponseDTO(
                post.getTitle(), postContent.getContent(), post.getImage(),
                true, tempPost.getId(), PostGuideMessage.EDIT_TEMP_POST_EXISTS.getMessage());
    }
}