package com.example.community.service;

import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.post.report.PostReport;
import com.example.community.exception.BlindedPostAccessException;
import com.example.community.exception.InvalidRequestException;
import com.example.community.exception.NotFoundException;
import com.example.community.repository.main.post.PostRepository;
import com.example.community.repository.main.post.report.PostReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final PostRepository postRepository;
    private final PostReportRepository postReportRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getReportedPostsInfo(int page) {
        if (page < 1) {
            throw new InvalidRequestException("invalid_page");
        }

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Post> postPage = postRepository.findReportedPageWithUser(pageable);

        List<Map<String, Object>> posts = postPage.getContent().stream()
                .map(post -> {
                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("post_id", post.getId());
                    postMap.put("post_title", post.getTitle());
                    postMap.put("post_image", post.getImage());
                    postMap.put("created_at", post.getCreatedAt());
                    postMap.put("like_count", post.getLikeCount());
                    postMap.put("view_count", post.getViewCount());
                    postMap.put("report_count", post.getReportCount());
                    postMap.put("user_id", post.getUser().getId());
                    postMap.put("user_nickname", post.getUser().getNickname());
                    postMap.put("user_image", post.getUser().getImage());
                    postMap.put("comment_count", post.getCommentCount());
                    return postMap;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("page", page);
        response.put("posts_count", posts.size());
        response.put("total_pages", postPage.getTotalPages());
        response.put("total_count", postPage.getTotalElements());

        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPostReportsInfo(Long postId) {
        validatePostAccessAvailable(postId);

        List<PostReport> postReports = postReportRepository.findByPostIdWithUser(postId);

        List<Map<String, Object>> reports = postReports.stream()
                .map(postReport -> {
                    Map<String, Object> reportMap = new HashMap<>();
                    reportMap.put("post_id", postReport.getPost().getId());
                    reportMap.put("user_id", postReport.getUser().getId());
                    reportMap.put("user_nickname", postReport.getUser().getNickname());
                    reportMap.put("user_image", postReport.getUser().getImage());
                    reportMap.put("reason", postReport.getReason());
                    reportMap.put("status", postReport.getStatus());
                    reportMap.put("reported_at", postReport.getReportedAt());
                    return reportMap;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("post_id", postId);
        response.put("reports", reports);
        response.put("reports_count", reports.size());

        return response;
    }

    @Transactional
    public void tryBlindPostProcess(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        validatePostAccessAvailable(post);
        List<PostReport> postReports = postReportRepository.findByPostId(postId);
        postReports.forEach(PostReport::accept);

        post.blind();
    }

    @Transactional
    public void tryRejectReportsInPostProcess(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException("post_deleted");
        }

        validatePostAccessAvailable(post);
        List<PostReport> postReports = postReportRepository.findByPostId(postId);
        postReports.forEach(PostReport::reject);

        post.unBlind();
    }

    private void validatePostAccessAvailable(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException("post_deleted");
        }

        if (post.getBlindedAt() != null) {
            throw new BlindedPostAccessException("already_blinded_post");
        }
    }

    private void validatePostAccessAvailable(Post post) {
        if (post.getDeletedAt() != null) {
            throw new NotFoundException("post_deleted");
        }

        if (post.getBlindedAt() != null) {
            throw new BlindedPostAccessException("already_blinded_post");
        }
    }
}