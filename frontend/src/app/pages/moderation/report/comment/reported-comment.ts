import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatMenuModule } from '@angular/material/menu';
import { ReportedPostPage } from '../post/reported-post';
import { CommentExtra } from '../../../../../types/comment';
import { API } from '../../../../lib/api';
import { ActivatedRoute } from '@angular/router';
@Component({
  templateUrl: 'reported-comment.html',
  imports: [MatButtonModule, MatFormFieldModule, MatMenuModule, ReportedPostPage],
  styleUrl: '../../../post/post.page.css',
})
export class ReportedCommentPage {
  comment = signal(new CommentExtra());
  route = inject(ActivatedRoute);

  constructor() {
    const reportId = this.route.snapshot.paramMap.get('reportId');
    if (!reportId) throw new Error('should provide reportId param');
    API.get<CommentExtra>(`/moderation/reports/${reportId}/comment`).then((c) =>
      this.comment.set(c),
    );
  }
}
