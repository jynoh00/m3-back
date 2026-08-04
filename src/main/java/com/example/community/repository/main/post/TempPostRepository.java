package com.example.community.repository.main.post;

import com.example.community.entity.main.post.TempPost;
import com.example.community.entity.main.post.TempPostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TempPostRepository extends JpaRepository<TempPost, TempPostId> {

    @Modifying
    @Query("DELETE FROM TempPost t WHERE t.id.userId = :userId And t.id.postId = :postId")
    void deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}