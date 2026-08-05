package com.example.community.dto.post.report;

import com.example.community.entity.main.post.report.PostReport;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostReportDTO {
    @JsonProperty("post_id")
    private final Long postId;

    @JsonProperty("user_id")
    private final Long userId;

    @JsonProperty("user_nickname")
    private final String userNickname;

    @JsonProperty("user_image")
    private final String userImage;

    @JsonProperty("reason")
    private final String reason;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("reported_at")
    private final LocalDateTime reportedAt;

    public PostReportDTO(PostReport postReport) {
        this.postId = postReport.getPost().getId();
        this.userId = postReport.getUser().getId();
        this.userNickname = postReport.getUser().getNickname();
        this.userImage = postReport.getUser().getImage();
        this.reason = postReport.getReason();
        this.status = postReport.getStatus();
        this.reportedAt = postReport.getReportedAt();
    }
}
