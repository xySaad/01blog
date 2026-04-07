import { Component, inject, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';
import { Post } from '../../../types/post';
import { API } from '../../lib/api';
import { ReportService } from '../../services/report.service';
import { UserService } from '../../services/user.service';
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
  data = input.required<Post>();
  comment = output();
  reportService = inject(ReportService);
  router = inject(Router);
  selfUser = inject(UserService).user;

  edit() {
    this.router.navigateByUrl(`/posts/edit/${this.data().id}`);
  }

  async like(liked: boolean) {
    const method = liked ? 'delete' : 'post';
    await API[method](`/posts/${this.data().id}/likes`, null);
  }
}
