import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { Component, inject, input, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardFooter } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { UserHeader } from '../user-header/header.component';
import { CommentExtra } from '../../../types/comment';
import { ReportService } from '../../services/report.service';
import { API } from '../../lib/api';

@Component({
  selector: 'comment-card',
  templateUrl: 'comment-card.html',
  styleUrl: 'comment-card.css',
  imports: [
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
export class CommentCard {
  comment = input.required<CommentExtra>();
  editComment = output<string>();
  reportService = inject(ReportService);
  editing = signal(false);

  async updateComment(commentId: string, content: string) {
    this.comment().content = content;
    await API.put(`/comments/${commentId}`, { content });
    this.editing.set(false);
  }
}
