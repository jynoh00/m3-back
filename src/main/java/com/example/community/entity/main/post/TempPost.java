package com.example.community.entity.main.post;

import com.example.community.entity.main.music.ArtistMusic;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "artist_id", referencedColumnName = "artist_id"),
            @JoinColumn(name = "music_id", referencedColumnName = "music_id")
    })
    private ArtistMusic artistMusic; // 초기 임시 저장 게시글의 경우 null 가능성 있음 => 허용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "post_id")
    private Long postId; // null이면 사용자 첫 작성 임시 게시글

    protected TempPost() {
    }

    public TempPost(String title, String content, ArtistMusic artistMusic, User user) {
        this.title = title;
        this.content = content;
        this.artistMusic = artistMusic;
        this.user = user;
        this.postId = null;
    }

    public void update(String title, String content, ArtistMusic artistMusic) {
        this.title = title;
        this.content = content;
        this.artistMusic = artistMusic;
    }

    public void connectPost(Long postId) {
        this.postId = postId;
    }
}