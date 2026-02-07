import { Component, input, OnInit, output, signal, WritableSignal } from '@angular/core';
import { MatCardHeader, MatCard, MatCardSubtitle, MatCardContent } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { WhileState } from '../../lib/decorators/loading';
import { global } from '../../lib/global';
import { Comment, CommentExtra } from '../../../types/comment';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { API } from '../../lib/api';

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

  comments = signal<CommentExtra[]>([]);
  loading = signal(false);
  editingId = signal('0');

  async ngOnInit() {
    const comments: CommentExtra[] = await API.get(`/posts/${this.postId()}/comments`);
    this.comments.set(comments);
  }

  @WhileState((self: CommentsList) => self.loading)
  async sendComment(content: string) {
    const comment: Comment = await API.post(`/posts/${this.postId()}/comments`, content);
    console.log(comment);

    this.comments.update((prev) => [{ ...comment, owner: global.user }, ...prev]);
  }

  @WhileState((self: CommentsList) => self.loading)
  async editComment(commentId: string, content: string) {
    this.editingId.set('0');

    await API.put(`/comments/${commentId}`, content);

    this.comments.update((prev) =>
      prev.map((c) => {
        if (c.id === commentId) c.content = content;
        return c;
      }),
    );
  }

  @WhileState((self: CommentsList) => self.loading)
  async deleteComment(commentId: string) {
    await API.delete(`/comments/${commentId}`);
    this.comments.update((prev) => prev.filter((c) => c.id !== commentId));
  }
}
