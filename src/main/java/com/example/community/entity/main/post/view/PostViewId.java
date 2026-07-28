package com.example.community.entity.main.post.view;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
public class PostViewId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    protected PostViewId() {
    }

    public PostViewId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}