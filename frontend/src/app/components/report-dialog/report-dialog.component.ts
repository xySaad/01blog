import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatAnchor } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput, MatLabel, MatPrefix } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { API } from '../../lib/api';
import { global } from '../../lib/global';
import { LoadingButton } from '../loading-button.component';

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
    LoadingButton,
  ],
})
export class ReportDialog {
  private dialogRef = inject(MatDialogRef<ReportDialog>);
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
