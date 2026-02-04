import { Component, input, output } from '@angular/core';
import { MatCardActions } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatAnchor } from '@angular/material/button';

@Component({
  selector: 'post-footer',
  templateUrl: 'footer.html',
  styleUrl: 'footer.css',
  imports: [MatCardActions, MatIcon, MatAnchor],
})
export class PostCardFooter {
  likesCount = input.required<number>();
  commentsCount = input.required<number>();
  readMore = output();
  like = output();
  comment = output();
}
