package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class JoinRequestDTO {
    @JsonProperty("user_email")
    @NotBlank(message = "email_required")
    @Email(message = "invalid_email_format")
    private String userEmail;

    @JsonProperty("user_nickname")
    @NotBlank(message = "nickname_required")
    @Size(min = 2, max = 10, message = "invalid_nickname_length")
    @Pattern(regexp = "^\\S+$", message = "invalid_nickname_blank")
    private String userNickname;


    @JsonProperty("user_password")
    @NotBlank(message = "password_required")
    @Size(min = 8, max = 20, message = "invalid_password_length")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S+$"
            , message = "invalid_password_format")
    private String userPassword;

    @JsonProperty("user_password_check")
    @NotBlank(message = "password_check_required")
    @Size(min = 8, max = 20, message = "invalid_password_check_length")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S+$"
            , message = "invalid_password_check_format")
    private String userPasswordCheck;

    @JsonProperty("user_image")
    private String userImage;
}