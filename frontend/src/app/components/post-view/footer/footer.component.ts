import { Component, inject, input, output } from '@angular/core';
import { MatCardActions } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatAnchor } from '@angular/material/button';
import { Router } from '@angular/router';

export interface PostFooterData {
  likesCount: number;
  commentsCount: number;
  id: string;
  liked: boolean;
}

@Component({
  selector: 'post-footer',
  templateUrl: 'footer.html',
  styleUrl: 'footer.css',
  imports: [MatCardActions, MatIcon, MatAnchor],
})
export class PostCardFooter {
  data = input.required<PostFooterData>();

  like = output<boolean>();
  comment = output();

  onLike() {
    const data = this.data();
    const { liked } = data;

    data.likesCount += liked ? -1 : 1;
    data.liked = !liked;
    this.like.emit(liked);
  }
  router = inject(Router);

  readMore() {
    this.router.navigateByUrl(`/posts/${this.data().id}`);
  }
}
