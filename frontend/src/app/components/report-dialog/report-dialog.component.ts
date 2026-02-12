import { Component, inject, signal } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogTitle,
  MatDialogClose,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatRadioModule } from '@angular/material/radio';
import { API } from '../../lib/api';
import { MatFormField, MatLabel, MatInput, MatPrefix } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { FormsModule } from '@angular/forms';
import { DeleteDialog } from '../../pages/post/edit/delete-dialog/delete-dialog.component';

type ReportReason = {
  text: string;
  value: string;
};

let REPORT_REASONS: ReportReason[] | null = null;
@Component({
  selector: 'report-dialog',
  templateUrl: 'report-dialog.html',
  styleUrl: 'report-dialog.css',
  imports: [
    MatDialogActions,
    MatRadioModule,
    MatDialogContent,
    MatDialogTitle,
    MatAnchor,
    MatDialogClose,
    MatFormField,
    MatLabel,
    MatIcon,
    MatInput,
    MatPrefix,
    CdkTextareaAutosize,
    FormsModule,
  ],
})
export class ReportDialog {
  private dialogRef = inject(MatDialogRef<DeleteDialog>);
  data: { id: string; item: string } = inject(MAT_DIALOG_DATA);
  reportReasons = signal<ReportReason[]>([]);
  selectedReason = '';
  description = signal('');

  constructor() {
    this.init();
  }

  async init() {
    if (!REPORT_REASONS) {
      const reportReasons = await API.get<string[]>(`/report`);
      REPORT_REASONS = reportReasons.map((r) => ({ text: this.snake2StartCase(r), value: r }));
    }

    this.reportReasons.set(REPORT_REASONS);
  }
  async submit() {
    const body = {
      type: this.data.item.toUpperCase(),
      id: this.data.id,
      reason: this.selectedReason,
      description: this.description(),
    };
    await API.post('/report', body);
    this.dialogRef.close();
  }
  snake2StartCase(text: string) {
    return text
      .split('_')
      .map((s) => s.charAt(0).toUpperCase() + s.slice(1))
      .join(' ');
  }
}
