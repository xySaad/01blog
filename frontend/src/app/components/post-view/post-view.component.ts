import { Component, inject, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';
import { Types } from '../../../types';
import { UserHeader } from '../user-header/header.component';
import { PostCardContent } from './content/content.component';
import { PostCardFooter } from './footer/footer.component';

@Component({
  selector: 'post-view',
  templateUrl: 'post-view.html',
  styleUrl: 'post-view.css',
  imports: [MatCardModule, MatButtonModule, UserHeader, PostCardContent, PostCardFooter],
})
export class PostView {
  router = inject(Router);
  data = input.required<Types.Post>();
  comment = output();

  edit() {
    this.router.navigateByUrl(`/posts/edit/${this.data().id}`);
  }

  like() {
    // let method: 'post' | 'delete' = this.liked ? 'delete' : 'post';
    // global.api[method](`/posts/${this.postData().id}/likes`, '');
  }
}
