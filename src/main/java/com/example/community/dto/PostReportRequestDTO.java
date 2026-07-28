package com.example.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostReportRequestDTO {
    @NotBlank(message = "report_reason_required")
    private String reason;
}