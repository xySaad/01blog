import { Routes } from '@angular/router';
import { Auth } from './pages/auth/auth';
import { Register } from './pages/auth/register/register';
import { Login } from './pages/auth/login/login';
import { NotFound } from './pages/404/404';
import { authGuard } from './guards/auth.guard';
import { PostEdit } from './pages/post/edit/post-edit';
import { PostsList } from './pages/user-posts/user-posts';
import { Home } from './pages/home/home';
import { PostPage } from './pages/post/post.page';
import { UserPage } from './pages/user/user-page';
import { PanelPage } from './pages/moderation/panel/panel-page';
import { moderationGuard } from './guards/moderation.guard';
import { ReportedPostPage } from './pages/moderation/report/post/reported-post';
import { ReportedCommentPage } from './pages/moderation/report/comment/reported-comment';
import { ReportedUserPage } from './pages/moderation/report/user/reported-user';
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
      return `/posts/edit/${id}`;
    },
  },
  { path: 'posts/:postId', component: PostPage, canActivate: [authGuard] },
  {
    path: 'posts',
    component: PostsList,
    canActivate: [authGuard],
  },
  {
    path: 'users/:id',
    component: UserPage,
    canActivate: [authGuard],
  },
  { path: 'moderation/panel', component: PanelPage, canMatch: [moderationGuard] },

  {
    path: 'moderation/reports/:reportId/post',
    component: ReportedPostPage,
    canActivate: [authGuard],
  },
  {
    path: 'moderation/reports/:reportId/comment',
    component: ReportedCommentPage,
    canActivate: [authGuard],
  },
  {
    path: 'moderation/reports/:reportId/user',
    component: ReportedUserPage,
    canActivate: [authGuard],
  },
  { path: '**', component: NotFound },
];
