package com.example.community.repository.main.comment;

import com.example.community.entity.main.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long postId);
}