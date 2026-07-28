package com.example.community.repository.main.user;

import com.example.community.entity.main.user.UserStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatRepository extends JpaRepository<UserStat, Long> {
}