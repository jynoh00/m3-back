package com.example.community.entity.main.post.like;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
public class PostLikeId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    protected PostLikeId() {
    }

    public PostLikeId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}