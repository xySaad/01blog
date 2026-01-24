import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { DB_NAME, Storage } from '../../../../services/storage.service';
import { Router } from '@angular/router';
import { MatProgressBar } from '@angular/material/progress-bar';

@Component({
  selector: 'delete-dialog',
  templateUrl: 'delete-dialog.html',
  imports: [MatDialogTitle, MatDialogContent, MatDialogActions, MatButtonModule, MatProgressBar],
  providers: [
    Storage,
    {
      provide: DB_NAME,
      useValue: 'blog',
    },
  ],
})
export class DeleteDialog {
  private dialogRef = inject(MatDialogRef<DeleteDialog>);
  data: { id: IDBValidKey | IDBKeyRange } = inject(MAT_DIALOG_DATA);
  db = inject(Storage);
  router = inject(Router);
  loading = signal(false);
  async delete() {
    this.loading.set(true);
    const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    postDrafts.delete(this.data.id);
    setTimeout(() => {
      this.loading.set(false);
      this.dialogRef.close(true);
      this.router.navigateByUrl('/posts');
    }, 3000);
  }
}
