SET NAMES utf8mb4;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NULL,
    nickname VARCHAR(50) NOT NULL,
    image VARCHAR(500) NULL DEFAULT 'https://jynoh00-m3-images.s3.ap-northeast-2.amazonaws.com/defaults/default-profile.png',
    deleted_at DATETIME(6) NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_nickname UNIQUE (nickname)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE artists (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_artists PRIMARY KEY (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE musics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    cover_image VARCHAR(500) NOT NULL,

    CONSTRAINT pk_musics PRIMARY KEY (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE artist_music (
    artist_id BIGINT NOT NULL,
    music_id BIGINT NOT NULL,

    CONSTRAINT pk_artist_music PRIMARY KEY (artist_id, music_id),
    INDEX idx_artist_music_music_id (music_id),

    CONSTRAINT fk_artist_music_artists
        FOREIGN KEY (artist_id)
        REFERENCES artists (id),

    CONSTRAINT fk_artist_music_musics
        FOREIGN KEY (music_id)
        REFERENCES musics (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE user_post_stats (
    id BIGINT NOT NULL,
    created_at DATETIME(6) NULL,
    `count` BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT pk_user_post_stats PRIMARY KEY (id),

    CONSTRAINT fk_user_post_stats_users
        FOREIGN KEY (id)
        REFERENCES users (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE temp_posts (
    user_id BIGINT NOT NULL,
    -- post_id=0: 연결된 게시글이 없는 신규 게시글의 임시 저장
    post_id BIGINT NOT NULL DEFAULT 0,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    artist_id BIGINT NULL,
    music_id BIGINT NULL,

    CONSTRAINT pk_temp_posts PRIMARY KEY (user_id, post_id),
    INDEX idx_temp_posts_artist_music (artist_id, music_id),

    CONSTRAINT fk_temp_posts_users
        FOREIGN KEY (user_id)
        REFERENCES users (id),

    CONSTRAINT fk_temp_posts_artist_music
        FOREIGN KEY (artist_id, music_id)
        REFERENCES artist_music (artist_id, music_id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE posts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(26) NOT NULL,
    content VARCHAR(100) NOT NULL,
    artist_id BIGINT NOT NULL,
    music_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NULL,
    like_count BIGINT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    report_count INT NOT NULL DEFAULT 0,
    blinded_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    comment_count BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT pk_posts PRIMARY KEY (id),
    INDEX idx_posts_user_id (user_id),
    INDEX idx_posts_artist_music (artist_id, music_id),

    CONSTRAINT fk_posts_users
        FOREIGN KEY (user_id)
        REFERENCES users (id),

    CONSTRAINT fk_posts_artist_music
        FOREIGN KEY (artist_id, music_id)
        REFERENCES artist_music (artist_id, music_id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE post_views (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    last_viewed_at DATETIME(6) NULL,

    CONSTRAINT pk_post_views PRIMARY KEY (user_id, post_id),
    INDEX idx_post_views_post_id (post_id),

    CONSTRAINT fk_post_views_users
        FOREIGN KEY (user_id)
        REFERENCES users (id),

    CONSTRAINT fk_post_views_posts
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE post_reports (
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reported_at DATETIME(6) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    CONSTRAINT pk_post_reports PRIMARY KEY (post_id, user_id),
    INDEX idx_post_reports_user_id (user_id),

    CONSTRAINT fk_post_reports_posts
        FOREIGN KEY (post_id)
        REFERENCES posts (id),

    CONSTRAINT fk_post_reports_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE post_likes (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,

    CONSTRAINT pk_post_likes PRIMARY KEY (user_id, post_id),
    INDEX idx_post_likes_post_id (post_id),

    CONSTRAINT fk_post_likes_users
        FOREIGN KEY (user_id)
        REFERENCES users (id),

    CONSTRAINT fk_post_likes_posts
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE comments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NULL,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    deleted_at DATETIME(6) NULL,

    CONSTRAINT pk_comments PRIMARY KEY (id),
    INDEX idx_comments_post_id (post_id),
    INDEX idx_comments_user_id (user_id),

    CONSTRAINT fk_comments_posts
        FOREIGN KEY (post_id)
        REFERENCES posts (id),

    CONSTRAINT fk_comments_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    expires_at DATETIME(6) NOT NULL,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    INDEX idx_refresh_tokens_user_id (user_id),

    CONSTRAINT fk_refresh_tokens_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE user_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    image VARCHAR(500) NULL,
    nickname VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    changed_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_user_histories PRIMARY KEY (id),
    INDEX idx_user_histories_user_id (user_id),

    CONSTRAINT fk_user_histories_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE post_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(26) NOT NULL,
    content VARCHAR(100) NOT NULL,
    artist_id BIGINT NOT NULL,
    music_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    changed_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_post_histories PRIMARY KEY (id),
    INDEX idx_post_histories_post_id (post_id),
    INDEX idx_post_histories_artist_music (artist_id, music_id),

    CONSTRAINT fk_post_histories_posts
        FOREIGN KEY (post_id)
        REFERENCES posts (id),

    CONSTRAINT fk_post_histories_artist_music
        FOREIGN KEY (artist_id, music_id)
        REFERENCES artist_music (artist_id, music_id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE comment_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(1000) NOT NULL,
    changed_at DATETIME(6) NOT NULL,
    comment_id BIGINT NOT NULL,

    CONSTRAINT pk_comment_histories PRIMARY KEY (id),
    INDEX idx_comment_histories_comment_id (comment_id),

    CONSTRAINT fk_comment_histories_comments
        FOREIGN KEY (comment_id)
        REFERENCES comments (id)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;
