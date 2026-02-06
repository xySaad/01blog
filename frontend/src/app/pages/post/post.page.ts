import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbar } from '@angular/material/toolbar';
import { ActivatedRoute } from '@angular/router';
import { Types } from '../../../types';
import { PostView } from '../../components/post-view/post-view.component';
import { global } from '../../lib/global';
import { CommentsList } from '../../components/comments-list/comments-list.component';
@Component({
  templateUrl: 'post.page.html',
  imports: [
    MatToolbar,
    MatIcon,
    MatButtonModule,
    MatFormFieldModule,
    MatMenuModule,
    PostView,
    CommentsList,
  ],
  styleUrl: 'post.page.css',
})
export class PostPage {
  private readonly route = inject(ActivatedRoute);
  private readonly id = this.route.snapshot.paramMap.get('id');

  protected readonly postData = signal(new Types.Post());

  constructor() {
    if (!this.id) throw new Error('should provide id param');
    const post = global.api.getJson(Types.Post, `/posts/${this.id}`);
    post.then(this.postData.set);
  }
}
