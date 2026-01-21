CREATE TABLE users (
    account_id bigint PRIMARY KEY REFERENCES accounts(id), -- accounts.id
    login varchar(12) UNIQUE NOT NULL, -- srm
    first_name text NOT NULL, -- saad
    last_name text NOT NULL -- rm
);