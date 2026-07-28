package com.example.community.entity.history.comment;

import com.example.community.entity.main.comment.Comment;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_histories")
@Getter
public class CommentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    protected CommentHistory() {
    }

    public CommentHistory(Comment comment, String content) {
        this.comment = comment;
        this.content = content;
        this.changedAt = LocalDateTime.now();
    }
}