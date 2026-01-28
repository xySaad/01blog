CREATE TABLE comments (
    id bigint PRIMARY KEY,
    post bigint NOT NULL REFERENCES posts(id),
    account bigint NOT NULL REFERENCES accounts(id),
    content text NOT NULL,
    deleted boolean NOT NULL
);