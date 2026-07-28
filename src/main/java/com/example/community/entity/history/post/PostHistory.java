package com.example.community.entity.history.post;

import com.example.community.entity.main.post.Post;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_histories")
@Getter
public class PostHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    protected PostHistory() {
    }

    public PostHistory(Post post, String title, String content, String image) {
        this.post = post;
        this.title = title;
        this.content = content;
        this.image = image;
        this.changedAt = LocalDateTime.now();
    }
}