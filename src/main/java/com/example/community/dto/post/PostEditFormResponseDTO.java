package com.example.community.dto.post;

import com.example.community.common.PostGuideMessage;
import com.example.community.dto.music.MusicSearchResultDTO;
import com.example.community.entity.main.music.ArtistMusic;
import com.example.community.entity.main.post.Post;
import com.example.community.entity.main.post.TempPost;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class PostEditFormResponseDTO {
    @JsonProperty("post_title")
    private final String postTitle;

    @JsonProperty("post_content")
    private final String postContent;

    @JsonProperty("music")
    private final MusicSearchResultDTO music;

    @JsonProperty("has_temp_post")
    private final boolean hasTempPost;

    @JsonProperty("temp_message")
    private final String tempMessage;

    private PostEditFormResponseDTO(
            String postTitle, String postContent, MusicSearchResultDTO music,
            boolean hasTempPost, String tempMessage) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.music = music;
        this.hasTempPost = hasTempPost;
        this.tempMessage = tempMessage;
    }

    public static PostEditFormResponseDTO of(Post post, List<ArtistMusic> artistMusicsOfMusic, TempPost tempPost) {
        MusicSearchResultDTO music = new MusicSearchResultDTO(post.getArtistMusic().getMusic(), artistMusicsOfMusic);

        if (tempPost == null) {
            return new PostEditFormResponseDTO(
                    post.getTitle(), post.getContent(), music, false, null);
        }

        return new PostEditFormResponseDTO(
                post.getTitle(), post.getContent(), music,
                true, PostGuideMessage.EDIT_TEMP_POST_EXISTS.getMessage());
    }
}