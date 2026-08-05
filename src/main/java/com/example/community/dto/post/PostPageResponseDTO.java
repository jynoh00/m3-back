package com.example.community.dto.post;

import com.example.community.entity.main.post.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PostPageResponseDTO {
    @JsonProperty("posts")
    private final List<PostSummaryDTO> posts;

    @JsonProperty("page")
    private final int page;

    @JsonProperty("posts_count")
    private final int postsCount;

    @JsonProperty("total_pages")
    private final int totalPages;

    @JsonProperty("total_count")
    private final long totalCount;

    private PostPageResponseDTO(
            List<PostSummaryDTO> posts, int page, int postsCount, int totalPages, long totalCount) {
        this.posts = posts;
        this.page = page;
        this.postsCount = postsCount;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
    }

    public static PostPageResponseDTO of(Page<Post> postPage, int page) {
        List<PostSummaryDTO> posts = postPage.getContent().stream()
                .map(PostSummaryDTO::new)
                .toList();

        return new PostPageResponseDTO(
                posts, page, posts.size(), postPage.getTotalPages(), postPage.getTotalElements());
    }
}