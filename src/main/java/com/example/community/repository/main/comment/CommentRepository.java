package com.example.community.repository.main.comment;

import com.example.community.entity.main.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long postId, Pageable pageable);
}