CREATE TABLE flight_requests
(
    id         BIGSERIAL       NOT NULL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    from_place VARCHAR(255) NOT NULL,
    to_place   VARCHAR(255) NOT NULL,
    from_date  DATE         NOT NULL,
    to_date    DATE,
    currency   VARCHAR(10)  NOT NULL,
    price      DECIMAL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);