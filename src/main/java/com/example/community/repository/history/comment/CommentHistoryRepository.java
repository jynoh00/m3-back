package com.example.community.repository.history.comment;

import com.example.community.entity.history.comment.CommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentHistoryRepository extends JpaRepository<CommentHistory, Long> {

}