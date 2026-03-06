import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';
import { Types } from '../../../types';
import { Collection } from '../../../types/collection';
import { PostCard } from '../../components/post-card/post-card.component';
import { API } from '../../lib/api';
import { UserService } from '../../services/user.service';
import { hasAdminAccess } from '../admin/admin-page';
@Component({
  templateUrl: 'home.html',
  styleUrl: 'home.css',
  imports: [MatButtonModule, MatIcon, PostCard],
})
export class Home {
  router = inject(Router);
  posts = signal<Types.Post[]>([]);
  readonly user = inject(UserService).user;
  readonly showAdmin = hasAdminAccess(this.user.permissions);
  constructor() {
    API.getH(Collection(Types.Post), '/posts').then(this.posts.set);
  }
}
