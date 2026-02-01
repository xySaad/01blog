CREATE TABLE post_likes (
    user_id bigint NOT NULL REFERENCES users(account_id),
    post_id bigint NOT NULL REFERENCES posts(id),
    PRIMARY KEY (user_id, post_id)
);