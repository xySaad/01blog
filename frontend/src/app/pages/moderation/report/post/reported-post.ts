import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbar } from '@angular/material/toolbar';
import { ActivatedRoute } from '@angular/router';
import { CommentsList } from '../../../../components/comments-list/comments-list.component';
import { PostView } from '../../../../components/post-view/post-view.component';
import { PostPage } from '../../../post/post.page';

@Component({
  selector: 'reported-comments-list',
  templateUrl: '../../../../components/comments-list/comments-list.html',
  styleUrl: '../../../../components/comments-list/comments-list.css',
  imports: CommentsList.Component.imports,
})
export class ReportedCommentsList extends CommentsList {
  public readonly route = inject(ActivatedRoute);
  public override get API_GET_ENDPOINT() {
    const reportId = this.route.snapshot.paramMap.get('reportId');
    if (!reportId) throw new Error('should provide reportId param');
    return `/moderation/reports/${reportId}/comments`;
  }
}

@Component({
  selector: 'reported-post-page',
  templateUrl: 'reported-post.html',
  imports: [
    MatToolbar,
    MatButtonModule,
    MatFormFieldModule,
    MatMenuModule,
    PostView,
    ReportedCommentsList,
  ],
  styleUrl: '../../../post/post.page.css',
})
export class ReportedPostPage extends PostPage {
  public override get API_ENDPOINT() {
    const reportId = this.route.snapshot.paramMap.get('reportId');
    if (!reportId) throw new Error('should provide reportId param');
    return `/moderation/reports/${reportId}/post`;
  }
}
