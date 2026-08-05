package com.example.community.dto.post.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ReportStatusResponseDTO {
    @JsonProperty("report_status")
    private final String reportStatus;

    public ReportStatusResponseDTO(String reportStatus) {
        this.reportStatus = reportStatus;
    }
}