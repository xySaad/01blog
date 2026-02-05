import { Component, inject, input, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardModule } from '@angular/material/card';
import { Types } from '../../../types';
import { PostView } from '../post-view/post-view.component';
import { Router } from '@angular/router';
import { CommentsSheet } from '../comments-sheet/comments-sheet.component';
import { MatBottomSheet } from '@angular/material/bottom-sheet';

@Component({
  imports: [MatCard, MatCardModule, MatButtonModule, PostView],
  selector: 'post-card',
  templateUrl: 'post-card.html',
  styleUrl: 'post-card.css',
})
export class PostCard implements OnInit {
  router = inject(Router);
  inputPost = input.required<Types.Post>({ alias: 'data' });
  visibility = input.required<'draft' | 'public' | 'private'>();
  isNew = input(false);

  postData = signal(new Types.Post());
  ngOnInit() {
    this.postData.set(this.inputPost());
  }
  readMore = () => {
    this.router.navigateByUrl(`/posts/${this.inputPost().id}`);
  };

  private bottomSheet = inject(MatBottomSheet);
  openCommentsSheet(): void {
    this.bottomSheet.open(CommentsSheet, {
      data: { postId: this.inputPost().id, onCommentsUpdate: this.commentsUpdate },
    });
  }

  commentsUpdate = (delta: number) => {
    console.log(delta);

    this.postData.update((prev) => {
      prev.commentsCount += delta;
      return prev;
    });
  };
}
