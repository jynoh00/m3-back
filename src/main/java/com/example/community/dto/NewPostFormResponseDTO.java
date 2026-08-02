package com.example.community.dto;

import com.example.community.common.PostGuideMessage;
import com.example.community.entity.main.post.TempPost;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class NewPostFormResponseDTO {
    @JsonProperty("has_temp_post")
    private final boolean hasTempPost;

    @JsonProperty("temp_post_id")
    private final Long tempPostId;

    @JsonProperty("message")
    private final String message;

    private NewPostFormResponseDTO(boolean hasTempPost, Long tempPostId, String message) {
        this.hasTempPost = hasTempPost;
        this.tempPostId = tempPostId;
        this.message = message;
    }

    public static NewPostFormResponseDTO noTempPost() {
        return new NewPostFormResponseDTO(false, null, null);
    }

    public static NewPostFormResponseDTO of(TempPost tempPost) {
        return new NewPostFormResponseDTO(
                true, tempPost.getId(), PostGuideMessage.NEW_TEMP_POST_EXISTS.getMessage());
    }
}
