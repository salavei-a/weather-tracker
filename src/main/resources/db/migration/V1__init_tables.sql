CREATE TABLE users
(
    id       BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(39)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE locations
(
    id        BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    name      VARCHAR(255) NOT NULL,
    user_id   BIGINT       NOT NULL,
    latitude  DECIMAL(8,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_locations_user_id_id ON locations(user_id, id);
CREATE UNIQUE INDEX uidx_locations_user_id_latitude_longitude ON locations(user_id, latitude, longitude);

CREATE TABLE sessions
(
    id         VARCHAR(36) NOT NULL,
    user_id    BIGINT      NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_expires_at ON sessions(expires_at);