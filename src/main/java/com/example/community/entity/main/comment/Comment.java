package com.example.community.entity.main.comment;

import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Comment() {

    }

    public Comment(Post post, User user, Comment parentComment, String content) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        this.post = post;
        this.user = user;
        this.parentComment = parentComment;
        this.content = content;
        this.deletedAt = null;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}