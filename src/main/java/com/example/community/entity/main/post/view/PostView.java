package com.example.community.entity.main.post.view;

import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_views")
@Getter
public class PostView {
    @EmbeddedId
    private PostViewId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;

    protected PostView() {
    }

    public PostView(User user, Post post) {
        this.id = new PostViewId(user.getId(), post.getId());
        this.user = user;
        this.post = post;
        this.lastViewedAt = LocalDateTime.now();
    }

    public void updateLastViewedAt() {
        this.lastViewedAt = LocalDateTime.now();
    }
}