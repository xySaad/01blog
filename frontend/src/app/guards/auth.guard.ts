import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { global } from '../lib/global';
import { API } from '../lib/api';
import { ApiError } from '../../types/api';

export const authGuard = async () => {
  const router = inject(Router);

  if (global.user.accountId != '') {
    return true;
  }

  try {
    global.user = await API.get('/me');

    return true;
  } catch (error) {
    if (error instanceof ApiError) {
      let step = error.status === 403 ? 'verify' : error.status === 404 ? 'profile' : undefined;
      if (step) return router.parseUrl(`/auth/register?step=${step}`);
    }

    const path = localStorage.getItem('lastLogin') === null ? 'register' : 'login';

    return router.parseUrl(`/auth/${path}`);
  }
};
