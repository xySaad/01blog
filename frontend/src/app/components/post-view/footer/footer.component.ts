import { Component, inject, input, output } from '@angular/core';
import { MatCardActions } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatAnchor } from '@angular/material/button';
import { Router } from '@angular/router';

@Component({
  selector: 'post-footer',
  templateUrl: 'footer.html',
  styleUrl: 'footer.css',
  imports: [MatCardActions, MatIcon, MatAnchor],
})
export class PostCardFooter {
  likesCount = input.required<number>();
  commentsCount = input.required<number>();
  postId = input.required<string>();

  like = output();
  comment = output();

  router = inject(Router);

  readMore() {
    this.router.navigateByUrl(`/posts/${this.postId()}`);
  }
}
