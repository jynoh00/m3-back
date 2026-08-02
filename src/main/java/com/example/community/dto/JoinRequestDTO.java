package com.example.community.dto;

import com.example.community.common.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class JoinRequestDTO {
    @JsonProperty("user_email")
    @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
    @Email(message = ValidationMessage.INVALID_EMAIL_FORMAT)
    private String userEmail;

    @JsonProperty("user_nickname")
    @NotBlank(message = ValidationMessage.NICKNAME_REQUIRED)
    @Size(min = 2, max = 10, message = ValidationMessage.INVALID_NICKNAME_LENGTH)
    @Pattern(regexp = ValidationMessage.NICKNAME_PATTERN, message = ValidationMessage.INVALID_NICKNAME_BLANK)
    private String userNickname;


    @JsonProperty("user_password")
    @NotBlank(message = ValidationMessage.PASSWORD_REQUIRED)
    @Size(min = 8, max = 20, message = ValidationMessage.INVALID_PASSWORD_LENGTH)
    @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN
            , message = ValidationMessage.INVALID_PASSWORD_FORMAT)
    private String userPassword;

    @JsonProperty("user_password_check")
    @NotBlank(message = ValidationMessage.PASSWORD_CHECK_REQUIRED)
    @Size(min = 8, max = 20, message = ValidationMessage.INVALID_PASSWORD_CHECK_LENGTH)
    @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN
            , message = ValidationMessage.INVALID_PASSWORD_CHECK_FORMAT)
    private String userPasswordCheck;

    @JsonProperty("user_image")
    private String userImage;
}