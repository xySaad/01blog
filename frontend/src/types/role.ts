export class Permission {
  id = 0;
  scope = '';
  description = '';
}

export class Role {
  id = 0;
  name = '';
  description = '';
  permissions: Permission[] = [];
}
