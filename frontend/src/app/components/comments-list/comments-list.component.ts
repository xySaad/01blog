import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { Component, inject, input, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardFooter } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { Comment, CommentExtra } from '../../../types/comment';
import { API } from '../../lib/api';
import { WhileState } from '../../lib/decorators/loading';
import { global } from '../../lib/global';
import { UserHeader } from '../user-header/header.component';
import { ReportDialog } from '../report-dialog/report-dialog.component';
import { MatDialog } from '@angular/material/dialog';

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
    UserHeader,
    CdkTextareaAutosize,
    MatCardFooter,
    MatInput,
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
    const comment: Comment = await API.post(`/posts/${this.postId()}/comments`, { content });

    this.comments.update((prev) => [{ ...comment, owner: global.user }, ...prev]);
  }

  @WhileState((self: CommentsList) => self.loading)
  async editComment(commentId: string, content: string) {
    this.editingId.set('0');

    await API.put(`/comments/${commentId}`, { content });

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

  private readonly dialog = inject(MatDialog);
  reportComment(commentId: string) {
    this.dialog.open(ReportDialog, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
      data: { id: commentId, item: 'comment' },
    });
  }
}
