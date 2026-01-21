CREATE TABLE sessions (
    account_id bigint PRIMARY KEY REFERENCES accounts, -- accounts.id
    jwt text NOT NULL
);