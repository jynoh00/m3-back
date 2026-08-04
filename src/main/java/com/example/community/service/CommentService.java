package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.common.PageRequestFactory;
import com.example.community.dto.CommentIdResponseDTO;
import com.example.community.dto.CommentPageResponseDTO;
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
import com.example.community.service.support.PostFinder;
import com.example.community.service.support.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentHistoryRepository commentHistoryRepository;

    private final PostFinder postFinder;
    private final UserFinder userFinder;

    @Transactional
    public CommentIdResponseDTO createCommentProcess(Long postId, CommentRequestDTO commentRequestDTO, Long userId) {
        Post post = postFinder.getActivePost(postId);
        User user = userFinder.getUser(userId);

        Comment comment = new Comment(
                post,
                user,
                commentRequestDTO.getCommentContent()
        );

        Comment savedComment = commentRepository.save(comment);
        post.increaseCommentCount();

        return new CommentIdResponseDTO(savedComment.getId());
    }

    @Transactional
    public void editCommentProcess(Long postId, Long commentId, CommentRequestDTO commentRequestDTO, Long userId) {
        postFinder.ensureExists(postId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getMessage()));

        if (comment.getPost().getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException(ExceptionMessage.COMMENT_NOT_IN_POST.getMessage());
        }

        if (comment.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getMessage());
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.COMMENT_EDIT_FORBIDDEN.getMessage());
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
        postFinder.ensureExists(postId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getMessage()));

        if (comment.getPost().getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        if (comment.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getMessage());
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException(ExceptionMessage.COMMENT_NOT_IN_POST.getMessage());
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.COMMENT_DELETE_FORBIDDEN.getMessage());
        }

        comment.delete(); // soft delete, 이후 더티체킹
        comment.getPost().decreaseCommentCount();
    }

    @Transactional(readOnly = true)
    public CommentPageResponseDTO getCommentsProcess(Long postId, Long userId, int page) {
        postFinder.getActivePost(postId);
        userFinder.getUser(userId);

        Pageable pageable = PageRequestFactory.of(page);
        Page<Comment> commentPage = commentRepository.findAllByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(postId, pageable);

        return CommentPageResponseDTO.of(commentPage, page);
    }
}