CREATE TABLE follows (
    user_id bigint NOT NULL REFERENCES users(account_id),
    follower_id bigint NOT NULL REFERENCES users(account_id),
    PRIMARY KEY (user_id, follower_id)
)