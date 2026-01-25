import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { DB_NAME, Storage } from '../../services/storage.service';
import { Router } from '@angular/router';
import { global } from '../../lib/global';
import { Post } from '../../components/post/post.component';
@Component({
  templateUrl: 'posts.html',
  imports: [Post],
  providers: [
    Storage,
    {
      provide: DB_NAME,
      useValue: 'blog',
    },
  ],
  styles: [
    `
      .posts {
        width: 100%;
        height: 100%;
        display: flex;
        flex-wrap: wrap;
        gap: 20px;

        .post {
          flex: 1;
        }
      }
    `,
  ],
})
export class Posts {
  router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private db = inject(Storage);

  drafts: any[] = [];
  savedPosts: any[] = [];
  constructor() {
    this.init();
  }

  async init() {
    const resp = await global.api.get('/posts');
    const posts = await resp.json();
    this.savedPosts = posts;

    const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    const req = postDrafts.getAll();
    req.onsuccess = () => {
      this.drafts = req.result;
      this.cdr.markForCheck();
    };
  }
}
