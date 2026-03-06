import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';

export const authGuard = async () => {
  const router = inject(Router);
  const { error } = inject(UserService);
  if (!error) return true;

  const { status } = error;
  const step = status === 403 ? 'verify' : status === 404 ? 'profile' : undefined;
  if (step) return router.parseUrl(`/auth/register?step=${step}`);

  const path = localStorage.getItem('lastLogin') === null ? 'register' : 'login';
  return router.parseUrl(`/auth/${path}`);
};
