CREATE TABLE users
(
    id                UUID         NOT NULL,
    email             VARCHAR(255) NOT NULL,
    password_hash     VARCHAR(255),
    display_name      VARCHAR(255) NOT NULL,
    avatar_url        VARCHAR(255),
    email_verified_at TIMESTAMPTZ,
    timezone          VARCHAR(255),
    status            VARCHAR(255) NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL,
    updated_at        TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE INDEX idx_users_status
    ON users (status);