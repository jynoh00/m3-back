package com.example.community.controller;

import com.example.community.common.ReportStatus;
import com.example.community.common.ResponseFormat;
import com.example.community.common.ResponseMessage;
import com.example.community.dto.LikeResponseDTO;
import com.example.community.dto.PostReportRequestDTO;
import com.example.community.security.TokenProvider;
import com.example.community.service.PostService;
import com.example.community.dto.PostRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController { // 게시글 관련 요청 처리
    private final PostService postService;
    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "1") int page) {
        Map<String, Object> pageInfo = postService.postsPageLoadProcess(page);

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.POSTS_PAGE_LOAD.getMessage(), pageInfo));
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostRequestDTO postRequestDTO, HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        Long postId = postService.createPostProcess(postRequestDTO, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseFormat.of(ResponseMessage.WRITE_POST_SUCCESS.getMessage(),
                        Map.of(
                                "post_id", postId
                        ))
        );
    }

    @GetMapping("/new")
    public ResponseEntity<?> getNewPostForm(HttpServletRequest request) {
        Long userId = tokenProvider.getUserId(request);

        Map<String, Object> response = postService.getNewPostFormProcess(userId);

        return ResponseEntity.ok(ResponseFormat.of(
                ResponseMessage.NEW_PAGE_LOAD.getMessage(),
                response
        ));
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<?> getPost(@PathVariable("post_id") Long postId,
                                     HttpServletRequest request) {
        Long userId = tokenProvider.getUserId(request);
        Map<String, Object> post = postService.getPostProcess(postId, userId);

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.POST_DETAILS_PAGE_LOAD.getMessage(), post));
    }

    @PatchMapping("/{post_id}")
    public ResponseEntity<?> updatePost(
            @PathVariable("post_id") Long postId, @Valid @RequestBody PostRequestDTO postRequestDTO,
            HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        postService.updatePostProcess(postId, postRequestDTO, userId);

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.POST_EDIT_PAGE_LOAD.getMessage(),
                Map.of(
                        "post_id", postId
                ))
        );
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> deletePost(@PathVariable("post_id") Long postId, HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        postService.deletePostProcess(postId, userId);

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.POST_DELETE_SUCCESS.getMessage()));
    }

    @GetMapping("/{post_id}/edit")
    public ResponseEntity<?> getPostEditForm(@PathVariable("post_id") Long postId, HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        Map<String, Object> postInfo = postService.getPostInfo(postId, userId);
        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.POST_EDIT_PAGE_LOAD.getMessage(), postInfo));
    }

    @PostMapping("/{post_id}/like")
    public ResponseEntity<?> addLike(@PathVariable("post_id") Long postId, HttpServletRequest request) {

        Long userId = tokenProvider.getUserId(request);
        LikeResponseDTO likeResponseDTO = postService.toggleLikeProcess(postId, userId);

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.LIKE_UPDATE_SUCCESS.getMessage(),
                Map.of(
                        "like_count", likeResponseDTO.getLikeCount(),
                        "increase_like_count", likeResponseDTO.isLike()
                )));
    }

    @PostMapping("/{post_id}/report")
    public ResponseEntity<?> reportPost(@PathVariable("post_id") Long postId, HttpServletRequest request,
                                        @RequestBody @Valid PostReportRequestDTO postReportRequestDTO) {
        Long userId = tokenProvider.getUserId(request);
        postService.reportPost(postId, userId, postReportRequestDTO.getReason());

        return ResponseEntity.ok(ResponseFormat.of(ResponseMessage.REPORT_POST_SUCCESS.getMessage(),
                Map.of(
                        "report_status", ReportStatus.PENDING.getStatus()
                )));
    }

    @PostMapping("/temp")
    public ResponseEntity<?> createTempPost(@RequestBody PostRequestDTO requestDTO, HttpServletRequest request) {
        Long userId = tokenProvider.getUserId(request);
        Long tempPostId = postService.createTempPostProcess(requestDTO, userId);

        return ResponseEntity.ok(
                ResponseFormat.of(
                        ResponseMessage.TEMP_POST_CREATE_SUCCESS.getMessage(),
                        Map.of("temp_post_id", tempPostId)
                )
        );
    }

    @PostMapping("/{post_id}/temp")
    public ResponseEntity<?> createPostEditTemp(@PathVariable("post_id") Long postId, @RequestBody PostRequestDTO requestDTO, HttpServletRequest request) {
        Long userId = tokenProvider.getUserId(request);
        Long tempPostId = postService.createPostEditTempProcess(postId, requestDTO, userId);

        return ResponseEntity.ok(
                ResponseFormat.of(
                        ResponseMessage.TEMP_POST_CREATE_SUCCESS.getMessage(),
                        Map.of("temp_post_id", tempPostId)
                )
        );
    }
}