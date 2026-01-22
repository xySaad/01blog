import { Routes } from '@angular/router';
import { Auth } from './pages/auth/auth';
import { Register } from './pages/auth/register/component';
import { Login } from './pages/auth/login/component';
import { Home } from './pages/home/component';
import { NotFound } from './pages/404/component';
import { authGuard } from './guards/auth.guard';

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
      {
        path: '',
        component: Login,
      },
    ],
  },
  {
    path: '',
    pathMatch: 'full',
    canActivate: [authGuard],
    component: Home,
  },
  { path: '**', component: NotFound },
];
