package com.example.community.entity.main.post.like;

import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "post_likes")
@Getter
public class PostLike {
    @EmbeddedId
    private PostLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    protected PostLike() {
    }

    public PostLike(User user, Post post) {
        this.id = new PostLikeId(user.getId(), post.getId());
        this.user = user;
        this.post = post;
    }
}