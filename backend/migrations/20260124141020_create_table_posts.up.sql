CREATE TABLE posts (
    id bigint PRIMARY KEY,
    account bigint REFERENCES accounts(id),
    title text NOT NULL,
    content text NOT NULL,

    public boolean NOT NULL,
    deleted boolean NOT NULL
);