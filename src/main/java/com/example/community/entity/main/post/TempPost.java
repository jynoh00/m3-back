package com.example.community.entity.main.post;

import com.example.community.entity.main.music.ArtistMusic;
import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "temp_posts")
@Getter
public class TempPost {

    public static final Long NO_POST_ID = 0L;

    @EmbeddedId
    private TempPostId id;

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

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected TempPost() {
    }

    public TempPost(String title, String content, ArtistMusic artistMusic, User user, Long postId) {
        this.id = new TempPostId(user.getId(), postId == null ? NO_POST_ID : postId);
        this.title = title;
        this.content = content;
        this.artistMusic = artistMusic;
        this.user = user;
    }

    public Long getPostId() {
        return NO_POST_ID.equals(id.getPostId()) ? null : id.getPostId();
    }

    public void update(String title, String content, ArtistMusic artistMusic) {
        this.title = title;
        this.content = content;
        this.artistMusic = artistMusic;
    }
}