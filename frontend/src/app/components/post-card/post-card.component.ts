import { Component, effect, inject, input, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardModule } from '@angular/material/card';
import { Types } from '../../../types';
import { PostView } from '../post-view/post-view.component';
import { Router } from '@angular/router';
import { CommentsSheet } from '../comments-sheet/comments-sheet.component';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { global } from '../../lib/global';

@Component({
  imports: [MatCard, MatCardModule, MatButtonModule, PostView],
  selector: 'post-card',
  templateUrl: 'post-card.html',
  styleUrl: 'post-card.css',
})
export class PostCard {
  data = input.required<Types.Post>({ alias: 'data' });
  router = inject(Router);
  selfUser = global.user;
  private bottomSheet = inject(MatBottomSheet);
  openCommentsSheet() {
    this.bottomSheet.open(CommentsSheet, {
      data: { postId: this.data().id, commentsCount: this.data().commentsCount },
    });
  }
}
