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
    id BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    image VARCHAR(500),
    user_id BIGINT NOT NULL,
    post_id BIGINT,

    CONSTRAINT pk_temp_posts PRIMARY KEY (id),

    CONSTRAINT fk_temp_posts_users
    FOREIGN KEY (user_id)
    REFERENCES users (id)
);

CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(255) NOT NULL,
    image VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    like_count BIGINT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    report_count INT NOT NULL DEFAULT 0,
    temp_id BIGINT NOT NULL,
    blinded_at TIMESTAMP,
    deleted_at TIMESTAMP,
    comment_count BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT pk_posts PRIMARY KEY (id),

    CONSTRAINT fk_posts_users
    FOREIGN KEY (user_id)
    REFERENCES users (id),

    CONSTRAINT fk_posts_temp_posts
    FOREIGN KEY (temp_id)
    REFERENCES temp_posts (id),

    CONSTRAINT uk_posts_temp_id UNIQUE (temp_id)
);

CREATE TABLE post_contents (
    id BIGINT NOT NULL,
    content TEXT NOT NULL,

    CONSTRAINT pk_post_contents PRIMARY KEY (id),

    CONSTRAINT fk_post_contents_posts
    FOREIGN KEY (id)
    REFERENCES posts (id)
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
    parent_comment_id BIGINT,
    content VARCHAR(1000) NOT NULL,
    deleted_at TIMESTAMP,

    CONSTRAINT pk_comments PRIMARY KEY (id),

    CONSTRAINT fk_comments_posts
    FOREIGN KEY (post_id)
    REFERENCES posts (id),

    CONSTRAINT fk_comments_users
    FOREIGN KEY (user_id)
    REFERENCES users (id),

    CONSTRAINT fk_comments_parent_comment
    FOREIGN KEY (parent_comment_id)
    REFERENCES comments (id)
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
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    image VARCHAR(500),
    post_id BIGINT NOT NULL,
    changed_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_post_histories PRIMARY KEY (id),

    CONSTRAINT fk_post_histories_posts
    FOREIGN KEY (post_id)
    REFERENCES posts (id)
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