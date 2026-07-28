package com.example.community.entity.history.user;

import com.example.community.entity.main.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_histories")
@Getter
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String image;

    @Column(nullable = false, length = 50)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    protected UserHistory() {
    }

    public UserHistory(User user, String nickname, String image) {
        this.user = user;
        this.nickname = nickname;
        this.image = image;
        this.changedAt = LocalDateTime.now();
    }
}