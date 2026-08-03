package com.example.community.entity.history.post;

import com.example.community.entity.main.music.ArtistMusic;
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

    @Column(nullable = false, length = 26)
    private String title;

    @Column(nullable = false, length = 100)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "artist_id", referencedColumnName = "artist_id", nullable = false),
            @JoinColumn(name = "music_id", referencedColumnName = "music_id", nullable = false)
    })
    private ArtistMusic artistMusic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    protected PostHistory() {
    }

    public PostHistory(Post post, String title, String content, ArtistMusic artistMusic) {
        this.post = post;
        this.title = title;
        this.content = content;
        this.artistMusic = artistMusic;
        this.changedAt = LocalDateTime.now();
    }
}