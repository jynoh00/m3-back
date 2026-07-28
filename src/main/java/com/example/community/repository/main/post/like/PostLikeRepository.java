package com.example.community.repository.main.post.like;

import com.example.community.entity.main.post.like.PostLike;
import com.example.community.entity.main.post.like.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

}
