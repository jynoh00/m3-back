package com.example.community.entity.main.auth;

import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500, nullable = false)
    private String token;

    protected RefreshToken() {
    }

    public RefreshToken(User user, LocalDateTime expiresAt, String token) {
        this.user = user;
        this.expiresAt = expiresAt;
        this.token = token;
    }
}