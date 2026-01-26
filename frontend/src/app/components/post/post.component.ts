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

@Component({
  selector: 'post',
  templateUrl: 'post.html',
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
  ],
  styles: [
    `
      .card {
        width: 100%;
      }
      .content {
        max-height: 40vh;
        overflow: hidden;
        min-width: 30vw;
      }
      .footer {
        margin-top: auto;
      }
    `,
  ],
})
export class Post {
  router = inject(Router);
  isNew = input(false);
  data = input.required<any>();
  type = input.required<string>();

  edit() {
    const queryParams = `new=${this.isNew()}&draft=${this.type() === 'draft'}`;
    this.router.navigateByUrl(`/posts/edit/${this.data().id}?${queryParams}`);
  }
}
