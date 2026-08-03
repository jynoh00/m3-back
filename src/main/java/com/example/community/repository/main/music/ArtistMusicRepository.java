package com.example.community.repository.main.music;

import com.example.community.entity.main.music.ArtistMusic;
import com.example.community.entity.main.music.ArtistMusicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtistMusicRepository extends JpaRepository<ArtistMusic, ArtistMusicId> {
    Optional<ArtistMusic> findByArtistIdAndMusicId(Long artistId, Long musicId);

    @Query("""
                select am
                from ArtistMusic am
                join fetch am.artist
                where am.music.id = :musicId
            """)
    List<ArtistMusic> findByMusicIdWithArtist(@Param("musicId") Long musicId);
}