import { Routes } from '@angular/router';
import { Auth } from './pages/auth/auth';
import { Register } from './pages/auth/register/register';
import { Login } from './pages/auth/login/login';
import { NotFound } from './pages/404/404';
import { authGuard } from './guards/auth.guard';
import { PostEdit } from './pages/posts/edit/post-edit';
import { PostsList } from './pages/posts/posts-list';
import { Home } from './pages/home/home';

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

  { path: 'posts/edit/:id', component: PostEdit, canActivate: [authGuard] },
  {
    path: 'posts/edit',
    redirectTo: () => {
      const id = crypto.randomUUID();
      return `/posts/edit/${id}?new=true&draft=true`;
    },
  },
  {
    path: 'posts',
    component: PostsList,
  },
  { path: '**', component: NotFound },
];
