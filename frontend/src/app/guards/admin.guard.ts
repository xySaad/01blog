import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';
import { hasAdminAccess } from '../pages/admin/admin-page';
import { UserService } from '../services/user.service';

export const adminGuard: CanMatchFn = () => {
  const user = inject(UserService).user;
  return hasAdminAccess(user.permissions);
};
