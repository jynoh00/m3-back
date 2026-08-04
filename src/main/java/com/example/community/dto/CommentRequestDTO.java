package com.example.community.dto;

import com.example.community.common.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequestDTO {
    @JsonProperty("comment_content")
    @NotBlank(message = ValidationMessage.COMMENT_CONTENT_REQUIRED)
    private String commentContent;
}
