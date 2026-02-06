import { Component, inject } from '@angular/core';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { MatListModule } from '@angular/material/list';
import { CommentsList } from '../comments-list/comments-list.component';

@Component({
  selector: 'bottom-sheet-overview-example-sheet',
  templateUrl: 'comments-sheet.html',
  imports: [MatListModule, CommentsList],
})
export class CommentsSheet {
  private bottomSheet = inject(MatBottomSheetRef);
  data: { postId: string; commentsCount: number } = inject(MAT_BOTTOM_SHEET_DATA);

  openLink(event: MouseEvent): void {
    this.bottomSheet.dismiss();
    event.preventDefault();
  }
}
