package com.example.community.dto;

import com.example.community.common.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostReportRequestDTO {
    @NotBlank(message = ValidationMessage.REPORT_REASON_REQUIRED)
    private String reason;
}