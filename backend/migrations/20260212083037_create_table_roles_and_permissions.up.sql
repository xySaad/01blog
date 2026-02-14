CREATE TABLE roles (
    id bigserial PRIMARY KEY,
    name varchar(50) UNIQUE NOT NULL,
    description text
);

CREATE TABLE permissions (
    id bigserial PRIMARY KEY,
    scope varchar(50) UNIQUE NOT NULL,
    description text
);

CREATE TABLE role_permissions (
    role_id bigint NOT NULL REFERENCES roles(id),
    permission_id bigint NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE account_roles (
    account_id bigint NOT NULL REFERENCES accounts(id),
    role_id bigint NOT NULL REFERENCES roles(id),
    PRIMARY KEY (account_id, role_id)
)
