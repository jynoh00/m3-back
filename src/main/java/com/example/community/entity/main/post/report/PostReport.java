package com.example.community.entity.main.post.report;

import com.example.community.common.ReportStatus;
import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_reports")
@Getter
public class PostReport {
    @EmbeddedId
    private PostReportId id;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, length = 50)
    private String status;

    protected PostReport() {
    }

    public PostReport(Post post, User user, String reason) {
        this.id = new PostReportId(post.getId(), user.getId());
        this.post = post;
        this.user = user;
        this.reason = reason;
        this.reportedAt = LocalDateTime.now();
        this.status = ReportStatus.PENDING.getStatus();
    }

    public void accept() {
        this.status = ReportStatus.ACCEPTED.getStatus();
    }

    public void reject() {
        this.status = ReportStatus.REJECTED.getStatus();
    }
}