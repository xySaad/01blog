import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';
import { hasPanelAccess } from '../pages/moderation/panel/panel-page';
import { UserService } from '../services/user.service';

export const moderationGuard: CanMatchFn = () => {
  const user = inject(UserService).user;
  return hasPanelAccess(user.permissions);
};
