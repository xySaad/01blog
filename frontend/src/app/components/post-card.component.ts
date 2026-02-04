import { Component, inject, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardModule } from '@angular/material/card';
import { Types } from '../../types';
import { PostView } from './post-view/post-view.component';
import { Router } from '@angular/router';

@Component({
  imports: [MatCard, MatCardModule, MatButtonModule, PostView],
  selector: 'post-card',
  template: `
    <mat-card>
      <post-view
        (readMore)="readMore()"
        [isNew]="isNew()"
        [data]="data()"
        [visibility]="data().visibility"
      ></post-view>
    </mat-card>
  `,
  styles: `
    :host ::ng-deep post-content {
      display: block;
      max-height: 40vh;
      overflow: hidden;
      min-width: 30vw;
    }
    :host ::ng-deep markdown img {
      display: none;
    }

    post-footer {
      margin-top: auto;
    }
  `,
})
export class PostCard {
  router = inject(Router);
  data = input.required<Types.Post>();
  visibility = input.required<'draft' | 'public' | 'private'>();
  isNew = input(false);

  readMore = () => {
    this.router.navigateByUrl(`/posts/${this.data().id}`);
  };
}
