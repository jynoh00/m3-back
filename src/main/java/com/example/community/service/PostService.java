package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.dto.LikeResponseDTO;
import com.example.community.dto.PostRequestDTO;
import com.example.community.dto.PostSummaryDTO;
import com.example.community.entity.history.post.PostHistory;
import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.post.PostContent;
import com.example.community.entity.main.post.TempPost;
import com.example.community.entity.main.post.like.PostLike;
import com.example.community.entity.main.post.like.PostLikeId;
import com.example.community.entity.main.post.report.PostReport;
import com.example.community.entity.main.post.report.PostReportId;
import com.example.community.entity.main.post.view.PostView;
import com.example.community.entity.main.post.view.PostViewId;
import com.example.community.entity.main.user.User;
import com.example.community.entity.main.user.UserStat;
import com.example.community.exception.*;
import com.example.community.repository.history.post.PostHistoryRepository;
import com.example.community.repository.main.post.PostContentRepository;
import com.example.community.repository.main.post.PostRepository;
import com.example.community.repository.main.post.TempPostRepository;
import com.example.community.repository.main.post.like.PostLikeRepository;
import com.example.community.repository.main.post.report.PostReportRepository;
import com.example.community.repository.main.post.view.PostViewRepository;
import com.example.community.repository.main.user.UserRepository;
import com.example.community.repository.main.user.UserStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TempPostRepository tempPostRepository;
    private final PostContentRepository postContentRepository;
    private final PostHistoryRepository postHistoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostReportRepository postReportRepository;
    private final PostViewRepository postViewRepository;
    private final UserStatRepository userStatRepository;

    @Transactional
    public Long createPostProcess(PostRequestDTO createPostRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        UserStat userStat = userStatRepository.findById(userId)
                .orElseGet(() -> userStatRepository.save(new UserStat(user)));

        LocalDateTime now = LocalDateTime.now();

        if (!userStat.canCreatePost(now)) {
            throw new InvalidRequestException(ExceptionMessage.POST_CREATE_LIMIT_EXCEEDED.getMessage());
        }

        TempPost tempPost = new TempPost(
                createPostRequestDTO.getPostTitle(),
                createPostRequestDTO.getPostContent(),
                createPostRequestDTO.getPostImage(),
                user
        );

        TempPost savedTempPost = tempPostRepository.save(tempPost);

        Post post = new Post(
                createPostRequestDTO.getPostTitle(),
                createPostRequestDTO.getPostImage(),
                user,
                savedTempPost
        );

        Post savedPost = postRepository.save(post);

        PostContent postContent = new PostContent(
                savedPost,
                createPostRequestDTO.getPostContent()
        );

        postContentRepository.save(postContent);

        userStat.recordPostCreation(now);
        tempPost.connectPost(savedPost.getId());

        return savedPost.getId();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> postsPageLoadProcess(int page) {
        if (page < 1) {
            throw new InvalidRequestException(ExceptionMessage.INVALID_PAGE.getMessage());
        }

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Post> postPage = postRepository.findPageWithUser(pageable);

        List<PostSummaryDTO> posts = postPage.getContent().stream()
                .map(PostSummaryDTO::new)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("page", page);
        response.put("posts_count", posts.size());
        response.put("total_pages", postPage.getTotalPages());
        response.put("total_count", postPage.getTotalElements());

        return response;
    }

    @Transactional
    public Map<String, Object> getPostProcess(Long postId, Long userId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        if (post.getBlindedAt() != null) {
            throw new BlindedPostAccessException(ExceptionMessage.CANNOT_ACCESS_BLINDED_POST.getMessage());
        }

        PostContent postContent = postContentRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_CONTENT_NOT_FOUND.getMessage()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        PostViewId postViewId = new PostViewId(userId, postId);

        Optional<PostView> postView = postViewRepository.findById(postViewId);

        LocalDateTime now = LocalDateTime.now();

        if (postView.isEmpty()) {
            postViewRepository.save(new PostView(user, post));
            post.increaseViewCount();
        } else {
            PostView view = postView.get();

            if (view.getLastViewedAt().plusHours(24).isBefore(now)) {
                post.increaseViewCount();
                view.updateLastViewedAt();
            }
        }

        // is_liked 응답 반환
        PostLikeId postLikeId = new PostLikeId(userId, postId);
        boolean isLiked = postLikeRepository.existsById(postLikeId);

        Map<String, Object> response = new HashMap<>();
        response.put("post_id", post.getId());
        response.put("post_title", post.getTitle());
        response.put("post_content", postContent.getContent());
        response.put("post_image", post.getImage());
        response.put("created_at", post.getCreatedAt());
        response.put("updated_at", post.getUpdatedAt());
        response.put("like_count", post.getLikeCount());
        response.put("is_liked", isLiked);
        response.put("view_count", post.getViewCount());
        response.put("report_count", post.getReportCount());
        response.put("blinded_at", post.getBlindedAt());
        response.put("comment_count", post.getCommentCount());

        response.put("user_id", post.getUser().getId());
        response.put("user_nickname", post.getUser().getNickname());
        response.put("user_image", post.getUser().getImage());

        return response;
    }

    @Transactional
    public void updatePostProcess(Long postId, PostRequestDTO postRequestDTO, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_UPDATE_FORBIDDEN.getMessage());
        }

        PostContent postContent = postContentRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_CONTENT_NOT_FOUND.getMessage()));

        PostHistory postHistory = new PostHistory(
                post,
                post.getTitle(),
                postContent.getContent(),
                post.getImage()
        );

        postHistoryRepository.save(postHistory);

        post.update(
                postRequestDTO.getPostTitle(),
                postRequestDTO.getPostImage()
        );

        postContent.updateContent(postRequestDTO.getPostContent());
    }

    @Transactional
    public void deletePostProcess(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_DELETE_FORBIDDEN.getMessage());
        }

        post.delete();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPostInfo(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_UPDATE_FORBIDDEN.getMessage());
        }

        PostContent postContent = postContentRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_CONTENT_NOT_FOUND.getMessage()));

        Map<String, Object> postInfo = new HashMap<>();
        postInfo.put("post_title", post.getTitle());
        postInfo.put("post_content", postContent.getContent());
        postInfo.put("post_image", post.getImage());

        Optional<TempPost> tempPost = tempPostRepository.findByPostId(postId);

        if (tempPost.isPresent()) {
            postInfo.put("has_temp_post", true);
            postInfo.put("temp_post_id", tempPost.get().getId());
            postInfo.put("temp_message", "수정 중인 임시 저장 글이 있습니다. 불러오시겠습니까?");
        } else {
            postInfo.put("has_temp_post", false);
        }

        return postInfo;
    }

    @Transactional
    public LikeResponseDTO toggleLikeProcess(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        PostLikeId postLikeId = new PostLikeId(userId, postId);

        Optional<PostLike> postLike = postLikeRepository.findById(postLikeId);

        if (postLike.isPresent()) {
            postLikeRepository.delete(postLike.get());
            post.decreaseLikeCount();

            return new LikeResponseDTO(post.getLikeCount(), false);
        }

        postLikeRepository.save(new PostLike(user, post));
        post.increaseLikeCount();

        return new LikeResponseDTO(post.getLikeCount(), true);
    }

    @Transactional
    public void reportPost(Long postId, Long userId, String reason) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        PostReportId postReportId = new PostReportId(postId, userId);

        if (postReportRepository.existsById(postReportId)) {
            throw new DuplicateResourceException(ExceptionMessage.POST_REPORT_ALREADY_EXISTS.getMessage());
        }

        PostReport postReport = new PostReport(post, user, reason);
        postReportRepository.save(postReport);

        post.increaseReportCount();
    }

    @Transactional
    public Long createTempPostProcess(PostRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        TempPost tempPost = new TempPost(
                requestDTO.getPostTitle(),
                requestDTO.getPostContent(),
                requestDTO.getPostImage(),
                user
        );

        return tempPostRepository.save(tempPost).getId();
    }

    @Transactional
    public Long createPostEditTempProcess(Long postId, PostRequestDTO requestDTO, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage());
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_UPDATE_FORBIDDEN.getMessage());
        }

        TempPost tempPost = tempPostRepository.findByPostId(postId)
                .orElseGet(() -> new TempPost(
                        requestDTO.getPostTitle(),
                        requestDTO.getPostContent(),
                        requestDTO.getPostImage(),
                        post.getUser()
                ));

        tempPost.update(
                requestDTO.getPostTitle(),
                requestDTO.getPostContent(),
                requestDTO.getPostImage()
        );

        tempPost.connectPost(postId);

        return tempPostRepository.save(tempPost).getId();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getNewPostFormProcess(Long userId) {
        Optional<TempPost> tempPost =
                tempPostRepository.findFirstByUserIdAndPostIdIsNullOrderByIdDesc(userId);

        Map<String, Object> response = new HashMap<>();

        if (tempPost.isPresent()) {
            response.put("has_temp_post", true);
            response.put("temp_post_id", tempPost.get().getId());
            response.put("message", "작성 중인 임시 저장 글이 있습니다. 불러오시겠습니까?");
            return response;
        }

        response.put("has_temp_post", false);
        return response;
    }

//    private boolean isBlinded() {
//
//    }
}
