import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { global } from '../lib/global';

export const authGuard = async () => {
  const router = inject(Router);

  if (global.user !== null) {
    return true;
  }

  const resp = await global.api.get('/user');
  if (resp.ok) {
    global.user = await resp.json();
    return true;
  }

  const path = localStorage.getItem('lastLogin') === null ? 'register' : 'login';
  return router.parseUrl(`/auth/${path}`);
};
