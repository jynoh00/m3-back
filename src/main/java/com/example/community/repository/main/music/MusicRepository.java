package com.example.community.repository.main.music;

import com.example.community.entity.main.music.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MusicRepository extends JpaRepository<Music, Long> {
    Optional<Music> findByTitle(String title);

    @Query("""
                select distinct m
                from Music m
                left join ArtistMusic am on am.music = m
                left join am.artist a
                where lower(m.title) like lower(concat('%', :keyword, '%'))
                   or lower(a.name) like lower(concat('%', :keyword, '%'))
                order by m.id desc
            """)
    List<Music> searchByKeyword(@Param("keyword") String keyword);
}
