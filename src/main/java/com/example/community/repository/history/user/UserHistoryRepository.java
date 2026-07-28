package com.example.community.repository.history.user;

import com.example.community.entity.history.user.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
}
