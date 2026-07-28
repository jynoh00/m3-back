package com.example.community.entity.main.post.report;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
public class PostReportId implements Serializable {

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    private Long userId;

    protected PostReportId() {
    }

    public PostReportId(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}