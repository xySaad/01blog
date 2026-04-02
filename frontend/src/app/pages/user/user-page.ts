import { Component, inject, signal } from '@angular/core';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatToolbar } from '@angular/material/toolbar';
import { ActivatedRoute } from '@angular/router';
import { Collection } from '../../../types/collection';
import { Post } from '../../../types/post';
import { UserExtra } from '../../../types/user';
import { PostCard } from '../../components/post-card/post-card.component';
import { UserHeader } from '../../components/user-header/header.component';
import { API } from '../../lib/api';
import { ReportService } from '../../services/report.service';

@Component({
  imports: [UserHeader, MatCard, PostCard, MatToolbar, MatCardContent],
  templateUrl: './user-page.html',
  styleUrl: './user-page.css',
})
export class UserPage {
  route = inject(ActivatedRoute);
  user = signal(new UserExtra());
  posts = signal<Post[]>([]);
  reportService = inject(ReportService);

  get API_ENDPOINT() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) throw new Error('must provide id param');
    return `/users/${id}`;
  }
  constructor() {
    API.get<UserExtra>(this.API_ENDPOINT).then(this.user.set);
    API.getH(Collection(Post), `${this.API_ENDPOINT}/posts`).then(this.posts.set);
  }
}
