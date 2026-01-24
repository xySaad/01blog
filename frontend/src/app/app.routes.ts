import { Routes } from '@angular/router';
import { Auth } from './pages/auth/auth';
import { Register } from './pages/auth/register/component';
import { Login } from './pages/auth/login/component';
import { NotFound } from './pages/404/component';
import { authGuard } from './guards/auth.guard';
import { NewPost } from './pages/posts/edit/posts-edit';
import { Posts } from './pages/posts/posts';
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

  { path: 'posts/edit/:id', component: NewPost, canActivate: [authGuard] },
  {
    path: 'posts/edit',
    redirectTo: () => {
      const id = crypto.randomUUID();
      return `/posts/edit/${id}`;
    },
  },
  {
    path: 'posts',
    component: Posts,
  },
  { path: '**', component: NotFound },
];
