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
import { WhileState } from '../../../../lib/decorators/loading';
import { global } from '../../../../lib/global';

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
  data: { id: IDBValidKey | IDBKeyRange; isDraft: boolean } = inject(MAT_DIALOG_DATA);
  db = inject(Storage);
  router = inject(Router);
  loading = signal(false);

  @WhileState((self) => self.loading)
  async delete() {
    if (this.data.isDraft) {
      const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
      postDrafts.delete(this.data.id);
    } else {
      await global.api.delete(`/posts/${this.data.id}`);
      //TODO: handle success and error
    }

    this.dialogRef.close(true);
    this.router.navigateByUrl('/posts');
  }
}
