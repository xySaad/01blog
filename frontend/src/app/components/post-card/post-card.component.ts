import { Component, inject, input } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';
import { Types } from '../../../types';
import { UserService } from '../../services/user.service';
import { CommentsSheet } from '../comments-sheet/comments-sheet.component';
import { PostView } from '../post-view/post-view.component';

@Component({
  imports: [MatCard, MatCardModule, MatButtonModule, PostView],
  selector: 'post-card',
  templateUrl: 'post-card.html',
  styleUrl: 'post-card.css',
})
export class PostCard {
  data = input.required<Types.Post>({ alias: 'data' });
  router = inject(Router);
  selfUser = inject(UserService).user;
  private bottomSheet = inject(MatBottomSheet);
  openCommentsSheet() {
    this.bottomSheet.open(CommentsSheet, {
      data: { postId: this.data().id, commentsCount: this.data().commentsCount },
    });
  }
}
