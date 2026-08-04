CREATE TABLE users (
    id BIGINT AUTO_INCREMENT NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    nickname VARCHAR(50) NOT NULL,
    image VARCHAR(500) DEFAULT '/images/default-profile.png',
    deleted_at TIMESTAMP,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_nickname UNIQUE (nickname)
);

CREATE TABLE artists (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_artists PRIMARY KEY (id)
);

CREATE TABLE musics (
    id BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(255) NOT NULL,
    cover_image VARCHAR(500) NOT NULL,

    CONSTRAINT pk_musics PRIMARY KEY (id)
);

CREATE TABLE artist_music (
    artist_id BIGINT NOT NULL,
    music_id BIGINT NOT NULL,

    CONSTRAINT pk_artist_music PRIMARY KEY (artist_id, music_id),

    CONSTRAINT fk_artist_music_artists
    FOREIGN KEY (artist_id)
    REFERENCES artists (id),

    CONSTRAINT fk_artist_music_musics
    FOREIGN KEY (music_id)
    REFERENCES musics (id)
);

CREATE TABLE user_post_stats (
    id BIGINT NOT NULL,
    created_at TIMESTAMP,
    count BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT pk_user_post_stats PRIMARY KEY (id),

    CONSTRAINT fk_user_post_stats_users
    FOREIGN KEY (id)
    REFERENCES users (id)
);

CREATE TABLE temp_posts (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL DEFAULT 0, -- 0이면 연결된 게시글이 없는 상태 => 신규 게시글 작성의 임시저장
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    artist_id BIGINT,
    music_id BIGINT,

    CONSTRAINT pk_temp_posts PRIMARY KEY (user_id, post_id),

    CONSTRAINT fk_temp_posts_users
    FOREIGN KEY (user_id)
    REFERENCES users (id),

    CONSTRAINT fk_temp_posts_artist_music
    FOREIGN KEY (artist_id, music_id)
    REFERENCES artist_music (artist_id, music_id)
);

CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(26) NOT NULL,
    content VARCHAR(100) NOT NULL,
    artist_id BIGINT NOT NULL,
    music_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    like_count BIGINT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    report_count INT NOT NULL DEFAULT 0,
    blinded_at TIMESTAMP,
    deleted_at TIMESTAMP,
    comment_count BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT pk_posts PRIMARY KEY (id),

    CONSTRAINT fk_posts_users
    FOREIGN KEY (user_id)
    REFERENCES users (id),

    CONSTRAINT fk_posts_artist_music
    FOREIGN KEY (artist_id, music_id)
    REFERENCES artist_music (artist_id, music_id)
);

CREATE TABLE post_views (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    last_viewed_at TIMESTAMP,

    CONSTRAINT pk_post_views PRIMARY KEY (user_id, post_id),

    CONSTRAINT fk_post_views_users
    FOREIGN KEY (user_id)
    REFERENCES users (id),

    CONSTRAINT fk_post_views_posts
    FOREIGN KEY (post_id)
    REFERENCES posts (id)
);

CREATE TABLE post_reports (
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reported_at TIMESTAMP NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    CONSTRAINT pk_post_reports PRIMARY KEY (post_id, user_id),

    CONSTRAINT fk_post_reports_posts
    FOREIGN KEY (post_id)
    REFERENCES posts (id),

    CONSTRAINT fk_post_reports_users
    FOREIGN KEY (user_id)
    REFERENCES users (id)
);

CREATE TABLE post_likes (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,

    CONSTRAINT pk_post_likes PRIMARY KEY (user_id, post_id),

    CONSTRAINT fk_post_likes_users
    FOREIGN KEY (user_id)
    REFERENCES users (id),

    CONSTRAINT fk_post_likes_posts
    FOREIGN KEY (post_id)
    REFERENCES posts (id)
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    deleted_at TIMESTAMP,

    CONSTRAINT pk_comments PRIMARY KEY (id),

    CONSTRAINT fk_comments_posts
    FOREIGN KEY (post_id)
    REFERENCES posts (id),

    CONSTRAINT fk_comments_users
    FOREIGN KEY (user_id)
    REFERENCES users (id)
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),

    CONSTRAINT fk_refresh_tokens_users
    FOREIGN KEY (user_id)
    REFERENCES users (id)
);

CREATE TABLE user_histories (
    id BIGINT AUTO_INCREMENT NOT NULL,
    image VARCHAR(500),
    nickname VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    changed_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_user_histories PRIMARY KEY (id),

    CONSTRAINT fk_user_histories_users
    FOREIGN KEY (user_id)
    REFERENCES users (id)
);

CREATE TABLE post_histories (
    id BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(26) NOT NULL,
    content VARCHAR(100) NOT NULL,
    artist_id BIGINT NOT NULL,
    music_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    changed_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_post_histories PRIMARY KEY (id),

    CONSTRAINT fk_post_histories_posts
    FOREIGN KEY (post_id)
    REFERENCES posts (id),

    CONSTRAINT fk_post_histories_artist_music
    FOREIGN KEY (artist_id, music_id)
    REFERENCES artist_music (artist_id, music_id)
);

CREATE TABLE comment_histories (
    id BIGINT AUTO_INCREMENT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    comment_id BIGINT NOT NULL,

    CONSTRAINT pk_comment_histories PRIMARY KEY (id),

    CONSTRAINT fk_comment_histories_comments
    FOREIGN KEY (comment_id)
    REFERENCES comments (id)
);