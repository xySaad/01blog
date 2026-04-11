import { Component, inject, input, TemplateRef, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { LoadingButton } from '../loading-button.component';

@Component({
  selector: 'confirmation-dialog',
  templateUrl: 'confirmation-dialog.html',
  imports: [
    MatButtonModule,
    MatExpansionModule,
    MatDialogContent,
    MatDialogActions,
    LoadingButton,
    MatDialogTitle,
    MatDialogClose,
  ],
  host: {
    '(click)': 'openDialog()',
  },
})
export class ConfirmationDialog<T extends unknown> {
  confirm = input.required<() => T>();
  cancel = input<() => T>();
  dialog = inject(MatDialog);
  templateRef = viewChild<TemplateRef<unknown>>(TemplateRef);

  dialogRef!: MatDialogRef<unknown, any>;
  openDialog() {
    const template = this.templateRef();
    if (!template) return;

    this.dialogRef = this.dialog.open(template, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
    });
  }
  async handleConfirm() {
    await this.confirm()();
    this.dialogRef.close();
  }
}
