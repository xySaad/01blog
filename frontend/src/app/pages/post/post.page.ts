import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbar } from '@angular/material/toolbar';
import { ActivatedRoute } from '@angular/router';
import { PostView } from '../../components/post-view/post-view.component';
import { CommentsList } from '../../components/comments-list/comments-list.component';
import { API } from '../../lib/api';
import { Post } from '../../../types/post';
@Component({
  templateUrl: 'post.page.html',
  imports: [MatToolbar, MatButtonModule, MatFormFieldModule, MatMenuModule, PostView, CommentsList],
  styleUrl: 'post.page.css',
})
export class PostPage {
  public get API_ENDPOINT() {
    const postId = this.route.snapshot.paramMap.get('postId');
    if (!postId) throw new Error('should provide postId param');
    return `/posts/${postId}`;
  }

  public readonly route = inject(ActivatedRoute);
  protected readonly postData = signal(new Post());

  constructor() {
    const post = API.getH(Post, this.API_ENDPOINT);
    post.then(this.postData.set);
  }
}
