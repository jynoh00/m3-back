package com.example.community.entity.main.post;

import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "temp_posts")
@Getter
public class TempPost {
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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "post_id")
    private Long postId; // null이면 사용자 첫 작성 임시 게시글

    protected TempPost() {
    }

    public TempPost(String title, String content, String image, User user) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.user = user;
        this.postId = null;
    }

    public void update(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public void connectPost(Long postId) {
        this.postId = postId;
    }
}