package com.example.community.repository.main.post.view;

import com.example.community.entity.main.post.view.PostView;
import com.example.community.entity.main.post.view.PostViewId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewRepository extends JpaRepository<PostView, PostViewId> {
}