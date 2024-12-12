CREATE TABLE flight_requests
(
    id         BIGSERIAL       NOT NULL PRIMARY KEY,
    user_id    BIGINT          NOT NULL,
    from_place TEXT   NOT NULL,
    to_place   TEXT   NOT NULL,
    from_date  DATE            NOT NULL,
    to_date    DATE,
    currency   TEXT     NOT NULL,
    price      DECIMAL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
