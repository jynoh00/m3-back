package com.example.community.entity.main.post;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "post_contents")
@Getter
public class PostContent {
    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Post post;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    protected PostContent() {
    }

    public PostContent(Post post, String content) {
        this.post = post;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}