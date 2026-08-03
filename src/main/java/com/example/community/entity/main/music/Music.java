package com.example.community.entity.main.music;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "musics")
@Getter
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "cover_image", nullable = false, length = 500)
    private String coverImage;

    protected Music() {
    }

    public Music(String title, String coverImage) {
        this.title = title;
        this.coverImage = coverImage;
    }
}