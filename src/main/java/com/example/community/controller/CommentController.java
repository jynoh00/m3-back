package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.common.ResponseMessage;
import com.example.community.security.TokenProvider;
import com.example.community.service.CommentService;
import com.example.community.dto.CommentRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{post_id}/comments")
public class CommentController { // 댓글 관련 요청 처리
    private final CommentService commentService;
    private final TokenProvider tokenProvider;

    @PostMapping
    public ResponseEntity<?> writeComment(
            @PathVariable("post_id") Long postId,
            @Valid @RequestBody() CommentRequestDTO commentRequestDTO, HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        Long commentId = commentService.createCommentProcess(postId, commentRequestDTO, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseFormat.of(ResponseMessage.WRITE_COMMENT_SUCCESS.getMessage(),
                        Map.of("comment_id", commentId)
                )
        );
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<?> editComment(
            @PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId,
            @Valid @RequestBody CommentRequestDTO commentRequestDTO, HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        commentService.editCommentProcess(postId, commentId, commentRequestDTO, userId);

        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.COMMENT_EDIT_SUCCESS.getMessage(),
                        Map.of("comment_id", commentId)
                )
        );
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId,
            HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        commentService.deleteCommentProcess(postId, commentId, userId);

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.COMMENT_DELETE_SUCCESS.getMessage()));
    }

    @GetMapping
    public ResponseEntity<?> getComments(
            @PathVariable("post_id") Long postId,
            HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);

        return ResponseEntity.ok(
                ResponseFormat.of(
                        ResponseMessage.GET_COMMENTS_SUCCESS.getMessage(),
                        Map.of("comments", commentService.getCommentsProcess(postId, userId))
                )
        );
    }
}