package com.example.community.dto.auth;

import com.example.community.common.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequestDTO {
    @JsonProperty("user_email")
    @NotBlank
    @Email(message = ValidationMessage.INVALID_EMAIL_FORMAT)
    private String userEmail;

    @JsonProperty("user_password")
    @NotBlank
    @Size(min = 8, max = 20, message = ValidationMessage.INVALID_PASSWORD_LENGTH)
    @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN
            , message = ValidationMessage.INVALID_PASSWORD_FORMAT)
    private String userPassword;
}
