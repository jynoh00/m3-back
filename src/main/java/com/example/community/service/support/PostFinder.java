package com.example.community.service.support;

import com.example.community.common.ExceptionMessage;
import com.example.community.entity.main.post.Post;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.main.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostFinder {
    private final PostRepository postRepository;

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));
    }

    public Post getPostWithUser(Long postId) {
        return postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));
    }

    public Post getActivePost(Long postId) {
        Post post = getPost(postId);
        ensureNotDeleted(post);

        return post;
    }

    public Post getActivePostWithUser(Long postId) {
        Post post = getPostWithUser(postId);
        ensureNotDeleted(post);

        return post;
    }

    public void ensureExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }
    }

    private void ensureNotDeleted(Post post) {
        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }
    }
}
