package com.example.community.entity.main.user;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_post_stats")
@Getter
public class UserStat {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long count;

    protected UserStat() {
    }

    public UserStat(User user) {
        this.user = user;
        this.createdAt = null;
        this.count = 0L;
    }

    public boolean canCreatePost(LocalDateTime now) {
        if (createdAt == null) {
            return true;
        }

        if (createdAt.plusMinutes(5).isBefore(now) || createdAt.plusMinutes(5).isEqual(now)) {
            return true;
        }

        return count < 3;
    }

    public void recordPostCreation(LocalDateTime now) {
        if (createdAt == null || !createdAt.plusMinutes(5).isAfter(now)) {
            this.createdAt = now;
            this.count = 1L;
            return;
        }

        this.count++;
    }
}