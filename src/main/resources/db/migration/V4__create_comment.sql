CREATE TABLE tb_comments
(
    id         UUID PRIMARY KEY,
    content    TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    author_id  UUID                     NOT NULL,
    post_id    UUID                     NOT NULL,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES tb_users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES tb_posts (id) ON DELETE CASCADE
);
