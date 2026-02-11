import { Component, inject, signal } from '@angular/core';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatToolbar } from '@angular/material/toolbar';
import { ActivatedRoute } from '@angular/router';
import { UserExtra } from '../../../types/user';
import { PostCard } from '../../components/post-card/post-card.component';
import { UserHeader } from '../../components/user-header/header.component';
import { API } from '../../lib/api';
import { MatDialog } from '@angular/material/dialog';
import { ReportDialog } from '../../components/report-dialog/report-dialog.component';

@Component({
  selector: 'user-page',
  imports: [UserHeader, MatCard, PostCard, MatToolbar, MatCardContent],
  templateUrl: './user-page.html',
  styleUrl: './user-page.css',
})
export class UserPage {
  route = inject(ActivatedRoute);
  user = signal(new UserExtra());

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) throw new Error('must provide id param');
    API.getH(UserExtra, `/user/${id}`).then(this.user.set);
  }

  private readonly dialog = inject(MatDialog);
  report() {
    this.dialog.open(ReportDialog, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
      data: { id: this.user().accountId, item: 'user' },
    });
  }
}
