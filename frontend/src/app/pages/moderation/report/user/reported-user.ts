import { Component } from '@angular/core';
import { MatCard, MatCardContent } from '@angular/material/card';
import { PostCard } from '../../../../components/post-card/post-card.component';
import { MatToolbar } from '@angular/material/toolbar';
import { UserHeader } from '../../../../components/user-header/header.component';
import { UserPage } from '../../../user/user-page';

@Component({
  templateUrl: '../../../../pages/user/user-page.html',
  styleUrl: '../../../../pages/user/user-page.css',
  imports: [UserHeader, MatCard, PostCard, MatToolbar, MatCardContent],
})
export class ReportedUserPage extends UserPage {
  override get API_ENDPOINT(): string {
    const reportId = this.route.snapshot.paramMap.get('reportId');
    if (!reportId) throw new Error('must provide reportId param');
    return `/moderation/reports/${reportId}/user`;
  }
}
