import { Component, inject, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MatCard,
  MatCardHeader,
  MatCardTitle,
  MatCardSubtitle,
  MatCardContent,
  MatCardActions,
  MatCardModule,
} from '@angular/material/card';
import { Router } from '@angular/router';
import { MarkdownComponent } from 'ngx-markdown';
import { Types } from '../../../types';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'post-card',
  templateUrl: 'post-card.html',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardSubtitle,
    MatCardContent,
    MatCardActions,
    MarkdownComponent,
    MatCardModule,
    MatButtonModule,
    MatIcon,
  ],
  styleUrl: 'post-card.css',
})
export class PostCard {
  router = inject(Router);
  isNew = input(false);
  data = input.required<Types.Post>();
  type = input.required<string>();

  edit() {
    const queryParams = `new=${this.isNew()}&draft=${this.type() === 'draft'}`;
    this.router.navigateByUrl(`/posts/edit/${this.data().id}?${queryParams}`);
  }

  readMore() {
    this.router.navigateByUrl(`/posts/${this.data().id}`);
  }

  previewContent() {
    return this.data()
      .content.split('\n\n')
      .filter((block) => !/!\[.*?\]\(.*?\)/.test(block))
      .slice(0, 5)
      .join('\n\n');
  }
}
