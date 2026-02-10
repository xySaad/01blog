import { Component, inject, signal } from '@angular/core';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatToolbar } from '@angular/material/toolbar';
import { ActivatedRoute } from '@angular/router';
import { UserExtra } from '../../../types/user';
import { PostCard } from '../../components/post-card/post-card.component';
import { UserHeader } from '../../components/user-header/header.component';
import { API } from '../../lib/api';
import { MatDivider } from '@angular/material/divider';

@Component({
  selector: 'user-page',
  imports: [UserHeader, MatCard, PostCard, MatToolbar, MatCardContent, MatDivider],
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
