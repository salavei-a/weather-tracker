CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(39)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS locations
(
    id        BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    name      VARCHAR(255) NOT NULL,
    user_id   BIGINT       NOT NULL,
    latitude  DECIMAL(8,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX idx_user_location_unique ON locations(user_id, latitude, longitude);

CREATE TABLE IF NOT EXISTS sessions
(
    id         VARCHAR(36) NOT NULL,
    user_id    BIGINT      NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_sessions_user_id ON sessions(user_id);