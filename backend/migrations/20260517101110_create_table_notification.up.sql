CREATE TABLE notifications (
    id bigint PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES users(account_id),
    type text NOT NULL,
    reference_id bigint,
    created_at timestamp NOT NULL,
    read boolean NOT NULL DEFAULT false
);