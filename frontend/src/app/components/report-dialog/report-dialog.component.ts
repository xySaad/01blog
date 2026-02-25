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
import { global } from '../../lib/global';

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
  reportReasons = global.reportReasons;
  selectedReason = '';
  description = signal('');

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
}
