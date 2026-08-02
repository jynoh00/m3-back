package com.example.community.dto;

import com.example.community.entity.main.post.report.PostReport;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class PostReportsResponseDTO {
    @JsonProperty("post_id")
    private final Long postId;

    @JsonProperty("reports")
    private final List<PostReportDTO> reports;

    @JsonProperty("reports_count")
    private final int reportsCount;

    private PostReportsResponseDTO(Long postId, List<PostReportDTO> reports) {
        this.postId = postId;
        this.reports = reports;
        this.reportsCount = reports.size();
    }

    public static PostReportsResponseDTO of(Long postId, List<PostReport> postReports) {
        List<PostReportDTO> reports = postReports.stream()
                .map(PostReportDTO::new)
                .toList();

        return new PostReportsResponseDTO(postId, reports);
    }
}