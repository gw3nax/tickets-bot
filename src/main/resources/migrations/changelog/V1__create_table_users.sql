CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    input_data_state TEXT
);