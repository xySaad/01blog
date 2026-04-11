ALTER TABLE account_roles
DROP CONSTRAINT account_roles_role_id_fkey;

ALTER TABLE account_roles
ADD CONSTRAINT account_roles_role_id_fkey
FOREIGN KEY (role_id)
REFERENCES roles(id);