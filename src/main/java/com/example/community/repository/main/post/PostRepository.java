package com.example.community.repository.main.post;

import com.example.community.entity.main.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(
            value = """
                    select p
                    from Post p
                    join fetch p.user
                    where p.deletedAt is null
                    and p.blindedAt is null
                    order by p.createdAt desc
                    """,
            countQuery = """
                    select count(p)
                    from Post p
                    where p.deletedAt is null
                    and p.blindedAt is null
                    """
    )
    Page<Post> findPageWithUser(Pageable pageable);

    @Query("""
                select p
                from Post p
                join fetch p.user
                where p.id = :postId
                  and p.deletedAt is null
            """)
    Optional<Post> findByIdWithUser(Long postId);

    @Query(
            value = """
                    select p
                    from Post p
                    join fetch p.user
                    where p.reportCount >= 5
                    and p.deletedAt is null
                    and p.blindedAt is null
                    order by p.reportCount desc, p.createdAt desc
                    """,
            countQuery = """
                    select count(p)
                    from Post p
                    where p.reportCount >= 5
                    and p.deletedAt is null
                    and p.blindedAt is null
                    """
    )
    Page<Post> findReportedPageWithUser(Pageable pageable);
}
