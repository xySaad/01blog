import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { global } from '../lib/global';

export const authGuard = async () => {
  const router = inject(Router);

  if (global.user !== null) {
    return true;
  }

  const resp = await global.api.get('/me');
  if (resp.ok) {
    global.user = await resp.json();
    return true;
  }

  let step;
  if (resp.status === 403) step = 'verify';
  if (resp.status === 404) step = 'profile';
  if (step) return router.parseUrl(`/auth/register?step=${step}`);

  const path = localStorage.getItem('lastLogin') === null ? 'register' : 'login';

  return router.parseUrl(`/auth/${path}`);
};
