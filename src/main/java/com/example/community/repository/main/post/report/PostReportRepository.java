package com.example.community.repository.main.post.report;

import com.example.community.entity.main.post.report.PostReport;
import com.example.community.entity.main.post.report.PostReportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport, PostReportId> {
    @Query("""
            select pr
            from PostReport pr
            join fetch pr.user
            where pr.post.id = :postId
            order by pr.reportedAt desc
            """)
    List<PostReport> findByPostIdWithUser(Long postId);

    @Query("""
            select pr
            from PostReport pr
            where pr.post.id = :postId
            """)
    List<PostReport> findByPostId(Long postId);
}