CREATE TABLE games
(
    id              UUID         NOT NULL,
    public_id       VARCHAR(255) NOT NULL,
    organizer_id    UUID         NOT NULL,
    title           VARCHAR(255) NOT NULL,
    sport           VARCHAR(255) NOT NULL,
    starts_at       TIMESTAMPTZ  NOT NULL,
    ends_at         TIMESTAMPTZ  NOT NULL,
    location        VARCHAR(255) NOT NULL,
    max_players     INTEGER      NOT NULL,
    status          VARCHAR(255) NOT NULL,
    cancelled_at    TIMESTAMPTZ,
    cost_per_player NUMERIC(10, 2),
    notes           VARCHAR(500),
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_games PRIMARY KEY (id),
    CONSTRAINT uk_games_public_id UNIQUE (public_id),
    CONSTRAINT ck_games_time_range
        CHECK (ends_at > starts_at),
    CONSTRAINT ck_games_max_players
        CHECK (max_players > 0),
    CONSTRAINT ck_games_cost_per_player
        CHECK (cost_per_player IS NULL OR cost_per_player >= 0),
    CONSTRAINT ck_games_status
        CHECK (status IN ('UPCOMING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT fk_games_organizer
        FOREIGN KEY (organizer_id)
            REFERENCES users (id)
);

CREATE INDEX idx_games_organizer_status_starts_at
    ON games (organizer_id, status, starts_at);