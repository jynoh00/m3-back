package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.common.PageRequestFactory;
import com.example.community.dto.*;
import com.example.community.entity.history.post.PostHistory;
import com.example.community.entity.main.music.ArtistMusic;
import com.example.community.entity.main.post.Post;
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
import com.example.community.repository.main.post.PostRepository;
import com.example.community.repository.main.post.TempPostRepository;
import com.example.community.repository.main.post.like.PostLikeRepository;
import com.example.community.repository.main.post.report.PostReportRepository;
import com.example.community.repository.main.post.view.PostViewRepository;
import com.example.community.repository.main.user.UserRepository;
import com.example.community.repository.main.user.UserStatRepository;
import com.example.community.service.support.MusicFinder;
import com.example.community.service.support.PostFinder;
import com.example.community.service.support.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TempPostRepository tempPostRepository;
    private final PostHistoryRepository postHistoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostReportRepository postReportRepository;
    private final PostViewRepository postViewRepository;
    private final UserStatRepository userStatRepository;

    private final PostFinder postFinder;
    private final UserFinder userFinder;
    private final MusicFinder musicFinder;

    @Transactional
    public PostIdResponseDTO createPostProcess(PostRequestDTO createPostRequestDTO, Long userId) {
        User user = userFinder.getUser(userId);

        UserStat userStat = userStatRepository.findById(userId)
                .orElseGet(() -> userStatRepository.save(new UserStat(user)));

        LocalDateTime now = LocalDateTime.now();

        if (!userStat.canCreatePost(now)) {
            throw new InvalidRequestException(ExceptionMessage.POST_CREATE_LIMIT_EXCEEDED.getMessage());
        }

        ArtistMusic artistMusic = musicFinder.resolveArtistMusic(
                createPostRequestDTO.getMusicTitle(),
                createPostRequestDTO.getCoverImage(),
                createPostRequestDTO.getArtistNames()
        );

        TempPost tempPost = new TempPost(
                createPostRequestDTO.getPostTitle(),
                createPostRequestDTO.getPostContent(),
                artistMusic,
                user
        );

        TempPost savedTempPost = tempPostRepository.save(tempPost);

        Post post = new Post(
                createPostRequestDTO.getPostTitle(),
                createPostRequestDTO.getPostContent(),
                artistMusic,
                user,
                savedTempPost
        );

        Post savedPost = postRepository.save(post);

        userStat.recordPostCreation(now);
        tempPost.connectPost(savedPost.getId());

        return new PostIdResponseDTO(savedPost.getId());
    }

    @Transactional(readOnly = true)
    public PostPageResponseDTO postsPageLoadProcess(int page) {
        Pageable pageable = PageRequestFactory.of(page);

        Page<Post> postPage = postRepository.findPageWithUser(pageable);

        return PostPageResponseDTO.of(postPage, page);
    }

    @Transactional
    public PostDetailResponseDTO getPostProcess(Long postId, Long userId) {
        Post post = postFinder.getActivePostWithUser(postId);

        if (post.getBlindedAt() != null) {
            throw new BlindedPostAccessException(ExceptionMessage.CANNOT_ACCESS_BLINDED_POST.getMessage());
        }

        User user = userFinder.getUser(userId);

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

        PostLikeId postLikeId = new PostLikeId(userId, postId);
        boolean isLiked = postLikeRepository.existsById(postLikeId);

        List<ArtistMusic> artistMusicsOfMusic = musicFinder.getArtistMusicsOf(post.getArtistMusic().getMusic());

        return new PostDetailResponseDTO(post, artistMusicsOfMusic, isLiked);
    }

    @Transactional
    public void updatePostProcess(Long postId, PostRequestDTO postRequestDTO, Long userId) {
        Post post = postFinder.getActivePost(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_UPDATE_FORBIDDEN.getMessage());
        }

        PostHistory postHistory = new PostHistory(
                post,
                post.getTitle(),
                post.getContent(),
                post.getArtistMusic()
        );

        postHistoryRepository.save(postHistory);

        ArtistMusic artistMusic = musicFinder.resolveArtistMusic(
                postRequestDTO.getMusicTitle(),
                postRequestDTO.getCoverImage(),
                postRequestDTO.getArtistNames()
        );

        post.update(
                postRequestDTO.getPostTitle(),
                postRequestDTO.getPostContent(),
                artistMusic
        );
    }

    @Transactional
    public void deletePostProcess(Long postId, Long userId) {
        Post post = postFinder.getActivePost(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_DELETE_FORBIDDEN.getMessage());
        }

        post.delete();
    }

    @Transactional(readOnly = true)
    public PostEditFormResponseDTO getPostInfo(Long postId, Long userId) {
        Post post = postFinder.getActivePost(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_UPDATE_FORBIDDEN.getMessage());
        }

        List<ArtistMusic> artisMusicsOfMusic = musicFinder.getArtistMusicsOf(post.getArtistMusic().getMusic());

        TempPost tempPost = tempPostRepository.findByPostId(postId).orElse(null);

        return PostEditFormResponseDTO.of(post, artisMusicsOfMusic, tempPost);
    }

    @Transactional
    public LikeResponseDTO toggleLikeProcess(Long postId, Long userId) {
        Post post = postFinder.getActivePost(postId);
        User user = userFinder.getUser(userId);

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
        Post post = postFinder.getActivePost(postId);
        User user = userFinder.getUser(userId);

        PostReportId postReportId = new PostReportId(postId, userId);

        if (postReportRepository.existsById(postReportId)) {
            throw new DuplicateResourceException(ExceptionMessage.POST_REPORT_ALREADY_EXISTS.getMessage());
        }

        PostReport postReport = new PostReport(post, user, reason);
        postReportRepository.save(postReport);

        post.increaseReportCount();
    }

    @Transactional
    public TempPostIdResponseDTO createTempPostProcess(PostRequestDTO requestDTO, Long userId) {
        User user = userFinder.getUser(userId);

        ArtistMusic artistMusic = musicFinder.resolveArtistMusic(
                requestDTO.getMusicTitle(),
                requestDTO.getCoverImage(),
                requestDTO.getArtistNames()
        );

        TempPost tempPost = new TempPost(
                requestDTO.getPostTitle(),
                requestDTO.getPostContent(),
                artistMusic,
                user
        );

        Long tempPostId = tempPostRepository.save(tempPost).getId();

        return new TempPostIdResponseDTO(tempPostId);
    }

    @Transactional
    public TempPostIdResponseDTO createPostEditTempProcess(Long postId, PostRequestDTO requestDTO, Long userId) {
        Post post = postFinder.getActivePost(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_UPDATE_FORBIDDEN.getMessage());
        }

        ArtistMusic artistMusic = musicFinder.resolveArtistMusic(
                requestDTO.getMusicTitle(),
                requestDTO.getCoverImage(),
                requestDTO.getArtistNames()
        );

        TempPost tempPost = tempPostRepository.findByPostId(postId)
                .orElseGet(() -> new TempPost(
                        requestDTO.getPostTitle(),
                        requestDTO.getPostContent(),
                        artistMusic,
                        post.getUser()
                ));

        tempPost.update(
                requestDTO.getPostTitle(),
                requestDTO.getPostContent(),
                artistMusic
        );

        tempPost.connectPost(postId);

        Long tempPostId = tempPostRepository.save(tempPost).getId();

        return new TempPostIdResponseDTO(tempPostId);
    }

    @Transactional(readOnly = true)
    public NewPostFormResponseDTO getNewPostFormProcess(Long userId) {
        Optional<TempPost> tempPost =
                tempPostRepository.findFirstByUserIdAndPostIdIsNullOrderByIdDesc(userId);

        return tempPost.map(NewPostFormResponseDTO::of)
                .orElseGet(NewPostFormResponseDTO::noTempPost);
    }

    @Transactional(readOnly = true)
    public TempPostDetailResponseDTO getNewPostTempDetailProcess(Long userId) {
        TempPost tempPost = tempPostRepository.findFirstByUserIdAndPostIdIsNullOrderByIdDesc(userId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.TEMP_POST_NOT_FOUND.getMessage()));

        return toTempPostDetailResponseDTO(tempPost);
    }

    @Transactional(readOnly = true)
    public TempPostDetailResponseDTO getPostEditTempDetailProcess(Long postId, Long userId) {
        Post post = postFinder.getActivePost(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new AuthorizationException(ExceptionMessage.POST_UPDATE_FORBIDDEN.getMessage());
        }

        TempPost tempPost = tempPostRepository.findByPostId(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.TEMP_POST_NOT_FOUND.getMessage()));

        return toTempPostDetailResponseDTO(tempPost);
    }

    private TempPostDetailResponseDTO toTempPostDetailResponseDTO(TempPost tempPost) {
        MusicSearchResultDTO music = null;

        if (tempPost.getArtistMusic() != null) {
            List<ArtistMusic> artistMusicsOfMusic = musicFinder.getArtistMusicsOf(tempPost.getArtistMusic().getMusic());
            music = new MusicSearchResultDTO(tempPost.getArtistMusic().getMusic(), artistMusicsOfMusic);
        }

        return new TempPostDetailResponseDTO(
                tempPost.getId(), tempPost.getTitle(), tempPost.getContent(), music);
    }
}
