package com.example.community.repository.history.post;

import com.example.community.entity.history.post.PostHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHistoryRepository extends JpaRepository<PostHistory, Long> {
}
