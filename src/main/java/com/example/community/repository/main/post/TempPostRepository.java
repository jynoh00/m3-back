package com.example.community.repository.main.post;

import com.example.community.entity.main.post.TempPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempPostRepository extends JpaRepository<TempPost, Long> {
    Optional<TempPost> findByPostId(Long postId);
    Optional<TempPost> findFirstByUserIdAndPostIdIsNullOrderByIdDesc(Long userId);
}