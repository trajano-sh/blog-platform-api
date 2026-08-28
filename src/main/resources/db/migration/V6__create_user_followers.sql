CREATE TABLE tb_user_followers
(
    user_id     UUID NOT NULL,
    follower_id UUID NOT NULL,
    PRIMARY KEY (user_id, follower_id),
    CONSTRAINT fk_user_followers_user FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_followers_follower FOREIGN KEY (follower_id) REFERENCES tb_users (id) ON DELETE CASCADE,
    CONSTRAINT ck_user_followers_no_self_follow CHECK (user_id <> follower_id)
);
