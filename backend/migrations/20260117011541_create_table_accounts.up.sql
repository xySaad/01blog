CREATE TABLE accounts (
    id bigint PRIMARY KEY, -- 2235468
    email text UNIQUE NOT NULL, -- srm@mail.com
    password_hash text NOT NULL, -- somepasswordhash

    verification_code integer UNIQUE, -- email verification
    code_created_at timestamp NOT NULL
);