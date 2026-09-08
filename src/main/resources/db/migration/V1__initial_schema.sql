CREATE TABLE users
(
    id                UUID         NOT NULL,
    email             VARCHAR(255) NOT NULL,
    password_hash     VARCHAR(255),
    display_name      VARCHAR(255) NOT NULL,
    avatar_url        VARCHAR(255),
    email_verified_at TIMESTAMPTZ,
    timezone          VARCHAR(255),
    status            VARCHAR(50)  NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL,
    updated_at        TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_users
        PRIMARY KEY (id),

    CONSTRAINT uk_users_email
        UNIQUE (email),

    CONSTRAINT ck_users_status
        CHECK (
            status IN ('ACTIVE', 'DISABLED')
            )
);

CREATE INDEX idx_users_status
    ON users (status);


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
    status          VARCHAR(50)  NOT NULL,
    cancelled_at    TIMESTAMPTZ,
    cost_per_player NUMERIC(10, 2),
    notes           VARCHAR(500),
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_games
        PRIMARY KEY (id),

    CONSTRAINT uk_games_public_id
        UNIQUE (public_id),

    CONSTRAINT fk_games_organizer
        FOREIGN KEY (organizer_id)
            REFERENCES users (id),

    CONSTRAINT ck_games_time_range
        CHECK (
            ends_at > starts_at
            ),

    CONSTRAINT ck_games_max_players
        CHECK (
            max_players > 0
            ),

    CONSTRAINT ck_games_cost_per_player
        CHECK (
            cost_per_player IS NULL
                OR cost_per_player >= 0
            ),

    CONSTRAINT ck_games_status
        CHECK (
            status IN (
                       'UPCOMING',
                       'COMPLETED',
                       'CANCELLED'
                )
            ),

    CONSTRAINT ck_games_cancelled_at
        CHECK (
            (status = 'CANCELLED' AND cancelled_at IS NOT NULL)
                OR
            (status <> 'CANCELLED' AND cancelled_at IS NULL)
            )
);

CREATE INDEX idx_games_organizer_status_starts_at
    ON games (
              organizer_id,
              status,
              starts_at
        );

CREATE INDEX idx_games_status_starts_at
    ON games (
              status,
              starts_at
        );


CREATE TABLE anonymous_identities
(
    id                 UUID        NOT NULL,
    claimed_by_user_id UUID,
    claimed_at         TIMESTAMPTZ,
    created_at         TIMESTAMPTZ NOT NULL,
    updated_at         TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_anonymous_identities
        PRIMARY KEY (id),

    CONSTRAINT fk_anonymous_identities_claimed_by_user
        FOREIGN KEY (claimed_by_user_id)
            REFERENCES users (id),

    CONSTRAINT ck_anonymous_identities_claim
        CHECK (
            (
                claimed_by_user_id IS NULL
                    AND claimed_at IS NULL
                )
                OR
            (
                claimed_by_user_id IS NOT NULL
                    AND claimed_at IS NOT NULL
                )
            )
);

CREATE INDEX idx_anonymous_identities_claimed_by_user
    ON anonymous_identities (claimed_by_user_id) WHERE claimed_by_user_id IS NOT NULL;


CREATE TABLE game_participants
(
    id                    UUID         NOT NULL,
    game_id               UUID         NOT NULL,
    user_id               UUID,
    anonymous_identity_id UUID,
    display_name          VARCHAR(255) NOT NULL,
    status                VARCHAR(50)  NOT NULL,
    joined_at             TIMESTAMPTZ  NOT NULL,
    updated_at            TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_game_participants
        PRIMARY KEY (id),

    CONSTRAINT fk_game_participants_game
        FOREIGN KEY (game_id)
            REFERENCES games (id),

    CONSTRAINT fk_game_participants_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT fk_game_participants_anonymous_identity
        FOREIGN KEY (anonymous_identity_id)
            REFERENCES anonymous_identities (id),

    CONSTRAINT ck_game_participants_identity
        CHECK (
            (
                user_id IS NOT NULL
                    AND anonymous_identity_id IS NULL
                )
                OR
            (
                user_id IS NULL
                    AND anonymous_identity_id IS NOT NULL
                )
            ),

    CONSTRAINT ck_game_participants_status
        CHECK (
            status IN (
                       'JOINED',
                       'CANCELLED'
                )
            )
);


CREATE UNIQUE INDEX uk_game_participants_game_user
    ON game_participants (
                          game_id,
                          user_id
        ) WHERE user_id IS NOT NULL;


CREATE UNIQUE INDEX uk_game_participants_game_anonymous
    ON game_participants (
                          game_id,
                          anonymous_identity_id
        ) WHERE anonymous_identity_id IS NOT NULL;


CREATE INDEX idx_game_participants_game
    ON game_participants (game_id);


CREATE INDEX idx_game_participants_user
    ON game_participants (user_id) WHERE user_id IS NOT NULL;


CREATE INDEX idx_game_participants_anonymous
    ON game_participants (anonymous_identity_id) WHERE anonymous_identity_id IS NOT NULL;