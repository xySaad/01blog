import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { Component, inject, input, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { Comment, CommentExtra } from '../../../types/comment';
import { API } from '../../lib/api';
import { UserService } from '../../services/user.service';
import { CommentCard } from '../comment-card/comment-card.component';
import { LoadingButton } from '../loading-button.component';

@Component({
  selector: 'comments-list',
  templateUrl: 'comments-list.html',
  styleUrl: 'comments-list.css',
  imports: CommentsList.Component.imports,
})
export class CommentsList implements OnInit {
  static Component = {
    imports: [
      MatIcon,
      MatButtonModule,
      MatFormFieldModule,
      MatMenuModule,
      CdkTextareaAutosize,
      MatInput,
      LoadingButton,
      CommentCard,
    ],
  };

  readonly user = inject(UserService).user;
  readonly postId = input.required<string>();
  comments = signal<CommentExtra[]>([]);
  editingId = signal('0');

  public get API_GET_ENDPOINT() {
    return `/posts/${this.postId()}/comments`;
  }

  async ngOnInit() {
    const comments: CommentExtra[] = await API.get(this.API_GET_ENDPOINT);
    this.comments.set(comments);
  }

  async sendComment(text: HTMLTextAreaElement) {
    const comment: Comment = await API.post(`/posts/${this.postId()}/comments`, {
      content: text.value,
    });
    const commentExtra = { ...comment, owner: this.user };
    this.comments.update((prev) => [commentExtra, ...prev]);
    text.value = '';
  }
}
