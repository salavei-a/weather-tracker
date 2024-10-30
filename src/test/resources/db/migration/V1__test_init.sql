CREATE TABLE users
(
    id       BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(39)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

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