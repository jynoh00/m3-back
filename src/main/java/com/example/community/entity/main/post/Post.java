package com.example.community.entity.main.post;

import com.example.community.entity.main.music.ArtistMusic;
import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
public class Post {
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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "report_count", nullable = false)
    private Integer reportCount;

    @ManyToOne(fetch = FetchType.LAZY) // OneToOne vs ManyToOne => ManyToOne 안정적 LAZY 때문에
    @JoinColumn(name = "temp_id", nullable = false, unique = true) // unique 추가
    private TempPost tempPost;

    @Column(name = "blinded_at")
    private LocalDateTime blindedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount;

    protected Post() {
    }

    public Post(String title, String content, ArtistMusic artistMusic, User user, TempPost tempPost) {
        this.title = title;
        this.content = content;
        this.artistMusic = artistMusic;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        this.likeCount = 0L;
        this.viewCount = 0L;
        this.user = user;
        this.reportCount = 0;
        this.tempPost = tempPost;
        this.blindedAt = null;
        this.deletedAt = null;
        this.commentCount = 0L;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void update(String title, String content, ArtistMusic artistMusic) {
        this.title = title;
        this.content = content;
        this.artistMusic = artistMusic;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void blind() {
        this.blindedAt = LocalDateTime.now();
    }

    public void unBlind() {
        this.blindedAt = null;
        this.reportCount = 0;
    }
}