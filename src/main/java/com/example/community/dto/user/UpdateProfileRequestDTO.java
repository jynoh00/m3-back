package com.example.community.dto.user;

import com.example.community.common.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateProfileRequestDTO {

    @JsonProperty("user_new_nickname")
    @NotBlank(message = ValidationMessage.NICKNAME_REQUIRED)
    @Size(min = 2, max = 10, message = ValidationMessage.INVALID_NICKNAME_LENGTH)
    @Pattern(regexp = ValidationMessage.NICKNAME_PATTERN, message = ValidationMessage.INVALID_NICKNAME_BLANK)
    private String userNewNickname;

    @JsonProperty("user_new_image")
    @NotBlank(message = ValidationMessage.USER_IMAGE_REQUIRED)
    private String userNewImage;
}
