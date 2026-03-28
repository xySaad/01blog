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
import { UserService } from '../../services/user.service';
import { LoadingButton } from '../loading-button.component';
import { UserHeader } from '../user-header/header.component';
import { ReportService } from '../../services/report.service';

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
    LoadingButton,
  ],
})
export class CommentsList implements OnInit {
  readonly user = inject(UserService).user;
  readonly postId = input.required<string>();
  reportService = inject(ReportService);

  comments = signal<CommentExtra[]>([]);
  loading = signal(false);
  editingId = signal('0');

  async ngOnInit() {
    const comments: CommentExtra[] = await API.get(`/posts/${this.postId()}/comments`);
    this.comments.set(comments);
  }

  @WhileState((self: CommentsList) => self.loading)
  async sendComment(text: HTMLTextAreaElement) {
    const content = text.value;
    const comment: Comment = await API.post(`/posts/${this.postId()}/comments`, { content });

    this.comments.update((prev) => [{ ...comment, owner: this.user }, ...prev]);
    text.value = '';
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
}
