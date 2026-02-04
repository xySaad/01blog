import { Component, input, OnInit, output, signal, WritableSignal } from '@angular/core';
import { MatCardHeader, MatCard, MatCardSubtitle, MatCardContent } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { WhileState } from '../../lib/decorators/loading';
import { global } from '../../lib/global';
import { Comment, CommentWithUser } from '../../../types/comment';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Collection } from '../../../types/collection';

@Component({
  selector: 'comments-list',
  templateUrl: 'comments-list.html',
  styleUrl: 'comments-list.css',
  imports: [
    MatIcon,
    MatCard,
    MatCardContent,
    MatButtonModule,
    MatFormFieldModule,
    MatMenuModule,
    MatCardHeader,
    MatCardSubtitle,
  ],
})
export class CommentsList implements OnInit {
  readonly user = global.user;
  readonly postId = input.required<string>();

  comments = signal<CommentWithUser[]>([]);
  loading = signal(false);
  editingId = signal('0');
  commentsUpdate = output<number>();

  ngOnInit() {
    const comments = global.api.getJson(
      Collection(CommentWithUser),
      `/posts/${this.postId()}/comments`,
    );
    comments.then(this.comments.set);
  }

  @WhileState((self: CommentsList) => self.loading)
  async sendComment(content: string) {
    const comment = await global.api.postJson(Comment, `/posts/${this.postId()}/comments`, content);
    this.comments.update((prev) => [{ user: global.user, comment }, ...prev]);
    this.commentsUpdate.emit(+1);
  }

  @WhileState((self: CommentsList) => self.loading)
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

  @WhileState((self: CommentsList) => self.loading)
  async deleteComment(commentId: string) {
    const resp = await global.api.delete(`/comments/${commentId}`);
    if (!resp.ok) {
      //TODO: handle errors
      return;
    }
    this.comments.update((prev) => prev.filter((c) => c.comment.id !== commentId));
    this.commentsUpdate.emit(-1);
  }
}
