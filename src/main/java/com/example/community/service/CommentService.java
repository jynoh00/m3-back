package com.example.community.service;

import com.example.community.dto.CommentRequestDTO;
import com.example.community.entity.history.comment.CommentHistory;
import com.example.community.entity.main.comment.Comment;
import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.user.User;
import com.example.community.exception.AuthorizationException;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.history.comment.CommentHistoryRepository;
import com.example.community.repository.main.comment.CommentRepository;
import com.example.community.repository.main.post.PostRepository;
import com.example.community.repository.main.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentHistoryRepository commentHistoryRepository;

    @Transactional
    public Long createCommentProcess(Long postId, CommentRequestDTO commentRequestDTO, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException("post_not_found");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        Comment parentComment = commentRequestDTO.getParentCommentId() == null
                ? null
                : commentRepository.findById(commentRequestDTO.getParentCommentId())
                  .orElseThrow(() -> new NotFoundException("comment_not_found"));

        Comment comment = new Comment(
                post,
                user,
                parentComment,
                commentRequestDTO.getCommentContent()
        );

        Comment savedComment = commentRepository.save(comment);
        post.increaseCommentCount();

        return savedComment.getId();
    }

    @Transactional
    public void editCommentProcess(Long postId, Long commentId, CommentRequestDTO commentRequestDTO, Long userId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("post_not_found");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment_not_found"));

        if (comment.getPost().getDeletedAt() != null) {
            throw new NotFoundException("post_not_found");
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException("comment_not_in_post");
        }

        if (comment.getDeletedAt() != null) {
            throw new NotFoundException("comment_not_found");
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new AuthorizationException("comment_edit_forbidden");
        }

        CommentHistory commentHistory = new CommentHistory(
                comment,
                comment.getContent()
        );

        commentHistoryRepository.save(commentHistory);
        comment.updateContent(commentRequestDTO.getCommentContent());
    }

    @Transactional
    public void deleteCommentProcess(Long postId, Long commentId, Long userId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("post_not_found");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment_not_found"));

        if (comment.getPost().getDeletedAt() != null) {
            throw new NotFoundException("post_not_found");
        }

        if (comment.getDeletedAt() != null) {
            throw new NotFoundException("comment_not_found");
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException("comment_not_in_post");
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new AuthorizationException("comment_delete_forbidden");
        }

        comment.delete(); // soft delete, 이후 더티체킹
        comment.getPost().decreaseCommentCount();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCommentsProcess(Long postId, Long userId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("post_not_found");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException("post_not_found");
        }


        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user_not_found"));

        List<Comment> comments = commentRepository.findAllByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> {
                    Map<String, Object> response = new HashMap<>();

                    response.put("comment_id", comment.getId());
                    response.put("comment_content", comment.getContent());
                    response.put("created_at", comment.getCreatedAt());
                    response.put("updated_at", comment.getUpdatedAt());

                    response.put("user_id", comment.getUser().getId());
                    response.put("user_nickname", comment.getUser().getNickname());
                    response.put("user_image", comment.getUser().getImage());

                    return response;
                })
                .toList();
    }
}