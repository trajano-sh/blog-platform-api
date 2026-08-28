CREATE TABLE tb_posts
(
    id         UUID PRIMARY KEY,
    title      VARCHAR(150)             NOT NULL,
    content    TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE,
    author_id  UUID                     NOT NULL,
    CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES tb_users (id) ON DELETE CASCADE
);

CREATE TABLE tb_post_likes
(
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (post_id, user_id),
    CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES tb_posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE
);