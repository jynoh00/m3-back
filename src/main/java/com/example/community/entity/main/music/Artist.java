package com.example.community.entity.main.music;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "artists")
@Getter
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    protected Artist() {
    }

    public Artist(String name) {
        this.name = name;
    }
}