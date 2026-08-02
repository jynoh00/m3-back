package com.example.community.service;

import com.example.community.common.ExceptionMessage;
import com.example.community.dto.PostPageResponseDTO;
import com.example.community.dto.PostReportsResponseDTO;
import com.example.community.dto.PostSummaryDTO;
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
    public PostPageResponseDTO getReportedPostsInfo(int page) {
        if (page < 1) {
            throw new InvalidRequestException(ExceptionMessage.INVALID_PAGE.getMessage());
        }

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Post> postPage = postRepository.findReportedPageWithUser(pageable);

        return PostPageResponseDTO.of(postPage, page);
    }

    @Transactional(readOnly = true)
    public PostReportsResponseDTO getPostReportsInfo(Long postId) {
        validatePostAccessAvailable(postId);

        List<PostReport> postReports = postReportRepository.findByPostIdWithUser(postId);

        return PostReportsResponseDTO.of(postId, postReports);
    }

    @Transactional
    public void tryBlindPostProcess(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        validatePostAccessAvailable(post);
        List<PostReport> postReports = postReportRepository.findByPostId(postId);
        postReports.forEach(PostReport::accept);

        post.blind();
    }

    @Transactional
    public void tryRejectReportsInPostProcess(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_DELETED.getMessage());
        }

        validatePostAccessAvailable(post);
        List<PostReport> postReports = postReportRepository.findByPostId(postId);
        postReports.forEach(PostReport::reject);

        post.unBlind();
    }

    private void validatePostAccessAvailable(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.POST_NOT_FOUND.getMessage()));

        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_DELETED.getMessage());
        }

        if (post.getBlindedAt() != null) {
            throw new BlindedPostAccessException(ExceptionMessage.ALREADY_BLINDED_POST.getMessage());
        }
    }

    private void validatePostAccessAvailable(Post post) {
        if (post.getDeletedAt() != null) {
            throw new NotFoundException(ExceptionMessage.POST_DELETED.getMessage());
        }

        if (post.getBlindedAt() != null) {
            throw new BlindedPostAccessException(ExceptionMessage.ALREADY_BLINDED_POST.getMessage());
        }
    }
}