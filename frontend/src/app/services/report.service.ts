import { inject, Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ReportDialog } from '../components/report-dialog/report-dialog.component';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly dialog = inject(MatDialog);

  report(data: { id: string; item: string }) {
    this.dialog.open(ReportDialog, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
      data,
    });
  }

  reportUser(id: string) {
    this.report({ id, item: 'user' });
  }

  reportPost(id: string) {
    this.report({ id, item: 'post' });
  }

  reportComment(id: string) {
    this.report({ id, item: 'comment' });
  }
}
