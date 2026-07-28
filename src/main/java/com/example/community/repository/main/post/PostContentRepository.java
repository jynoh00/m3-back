package com.example.community.repository.main.post;

import com.example.community.entity.main.post.PostContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostContentRepository extends JpaRepository<PostContent, Long> {
}