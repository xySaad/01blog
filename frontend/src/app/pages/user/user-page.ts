import { Component, inject, signal } from '@angular/core';
import { UserHeader } from '../../components/user-header/header.component';
import { UserExtra } from '../../../types/user';
import { ActivatedRoute } from '@angular/router';
import { MatCard } from '@angular/material/card';
import { PostCard } from '../../components/post-card/post-card.component';
import { API } from '../../lib/api';
import { MatToolbar } from '@angular/material/toolbar';

@Component({
  selector: 'user-page',
  imports: [UserHeader, MatCard, PostCard, MatToolbar],
  templateUrl: './user-page.html',
  styleUrl: './user-page.css',
})
export class UserPage {
  route = inject(ActivatedRoute);
  user = signal(new UserExtra());

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) throw new Error('must provide id param');
    API.getH(UserExtra, `/user/${id}`).then(this.user.set);
  }
}
