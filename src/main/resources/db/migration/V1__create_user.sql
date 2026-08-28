CREATE TABLE tb_users
(
    id         UUID PRIMARY KEY,
    username   varchar(50)              NOT NULL,
    email      varchar(100)             NOT NULL,
    password   varchar(255)             NOT NULL,
    bio        TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_username UNIQUE (username),
    CONSTRAINT uk_user_email UNIQUE (email)
);
