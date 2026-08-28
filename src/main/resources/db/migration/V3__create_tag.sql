CREATE TABLE tb_tags
(
    id   UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT uk_tag_name UNIQUE (name)
);

CREATE TABLE tb_post_tags
(
    post_id UUID NOT NULL,
    tag_id  UUID NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) REFERENCES tb_posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_tags_tag FOREIGN KEY (tag_id) REFERENCES tb_tags (id) ON DELETE CASCADE
);