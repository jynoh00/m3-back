package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateProfileRequestDTO {

    @JsonProperty("user_new_nickname")
    @NotBlank(message = "nickname_required")
    @Size(min = 2, max = 10, message = "invalid_nickname_length")
    @Pattern(regexp = "^\\S+$", message = "invalid_nickname_blank")
    private String userNewNickname;

    @JsonProperty("user_new_image")
    @NotBlank(message = "user_image_required")
    private String userNewImage;
}
