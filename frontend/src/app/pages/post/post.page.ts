import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { global } from '../../lib/global';
import { Types } from '../../../types';
import { PostView } from '../../components/post-view/post-view';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { MatCard, MatCardHeader, MatCardSubtitle, MatCardContent } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { Collection } from '../../../types/collection';
import { WhileState } from '../../lib/decorators/loading';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatMenuModule } from '@angular/material/menu';
@Component({
  templateUrl: 'post.page.html',
  imports: [
    PostView,
    MatToolbar,
    MatIcon,
    MatCard,
    MatCardHeader,
    MatCardSubtitle,
    MatCardContent,
    MatButtonModule,
    MatFormFieldModule,
    MatMenuModule,
  ],
  styleUrl: 'post.page.css',
})
export class PostPage {
  loading = signal(false);
  private readonly route = inject(ActivatedRoute);
  postData = signal(new Types.Post());
  comments = signal<Types.CommentWithUser[]>([]);
  user = global.user;
  id;
  editingId = signal('0');

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) throw new Error('should provide id param');
    this.id = id;
    const post = global.api.getJson(Types.Post, `/posts/${id}`);
    post.then(this.postData.set);

    const comments = global.api.getJson(Collection(Types.CommentWithUser), `/posts/${id}/comments`);
    comments.then(this.comments.set);
  }

  @WhileState((self: PostPage) => self.loading)
  async sendComment(content: string) {
    const comment = await global.api.postJson(Types.Comment, `/posts/${this.id}/comments`, content);
    this.comments.update((prev) => [{ user: global.user, comment }, ...prev]);
  }

  @WhileState((self: PostPage) => self.loading)
  async editComment(commentId: string, content: string) {
    this.editingId.set('0');

    await global.api.put(`/comments/${commentId}`, content);

    this.comments.update((prev) =>
      prev.map((c) => {
        if (c.comment.id === commentId) c.comment.content = content;
        return c;
      }),
    );
  }

  @WhileState((self: PostPage) => self.loading)
  async deleteComment(id: string) {
    const resp = await global.api.delete(`/comments/${id}`);
    if (!resp.ok) {
      //TODO: handle errors
      return;
    }
    this.comments.update((prev) => prev.filter((c) => c.comment.id !== id));
  }
}
