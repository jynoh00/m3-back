package com.example.community.entity.main.user;

import com.example.community.common.UserRole;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false, length = 50, unique = true)
    private String nickname;

    @Column(length = 500)
    private String image;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "role", nullable = false, length = 20)
    private String role = UserRole.USER.getRole();

    protected User() {
    }

    public User(String email, String password, String nickname, String image) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.deletedAt = null;
        this.role = UserRole.USER.getRole();

        if (image != null) {
            this.image = image;
            return;
        }
        this.image = "/images/default-profile.png";
    }

    public void delete() {
        if (this.deletedAt != null) return;

        this.email = "deleted_user_" + this.id + "@deleted.local";
        this.nickname = "deleted_user_" + this.id;
        this.image = null;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateProfile(String nickname, String image) {
        this.nickname = nickname;
        this.image = image;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}