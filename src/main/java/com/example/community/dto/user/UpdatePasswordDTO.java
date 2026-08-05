package com.example.community.dto.user;

import com.example.community.common.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdatePasswordDTO {
    @JsonProperty("user_new_password")
    @NotBlank(message = ValidationMessage.PASSWORD_REQUIRED)
    @Size(min = 8, max = 20, message = ValidationMessage.INVALID_PASSWORD_LENGTH)
    @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN
            , message = ValidationMessage.INVALID_PASSWORD_FORMAT)
    private String userNewPassword;

    @JsonProperty("user_new_password_check")
    @NotBlank(message = ValidationMessage.PASSWORD_CHECK_REQUIRED)
    @Size(min = 8, max = 20, message = ValidationMessage.INVALID_PASSWORD_CHECK_LENGTH)
    @Pattern(regexp = ValidationMessage.PASSWORD_PATTERN
            , message = ValidationMessage.INVALID_PASSWORD_CHECK_FORMAT)
    private String userNewPasswordCheck;
}
