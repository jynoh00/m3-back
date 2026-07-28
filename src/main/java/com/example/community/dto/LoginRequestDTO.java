package com.example.community.dto;

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
    @Email(message = "invalid_email_format")
    private String userEmail;

    @JsonProperty("user_password")
    @NotBlank
    @Size(min = 8, max = 20, message = "invalid_password_length")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S+$"
            , message = "invalid_password_format")
    private String userPassword;
}
