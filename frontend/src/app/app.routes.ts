import { Routes } from '@angular/router';
import { Auth } from './pages/auth/auth';
import { Register } from './pages/auth/register/component';
import { Login } from './pages/auth/login/component';
import { Home } from './pages/home/component';

export const routes: Routes = [
  {
    path: 'auth',
    component: Auth,
    children: [
      {
        path: 'register',
        component: Register,
      },
      {
        path: 'login',
        component: Login,
      },
    ],
  },
  {
    path: 'home',
    component: Home,
  },
];
