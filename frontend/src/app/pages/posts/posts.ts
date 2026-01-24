import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { DB_NAME, Storage } from '../../services/storage.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MarkdownComponent } from 'ngx-markdown';
import { Router } from '@angular/router';

@Component({
  templateUrl: 'posts.html',
  imports: [MatCardModule, MatButtonModule, MarkdownComponent],
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
        display: flex;
        gap: 20px;

        .post {
          overflow: hidden;
          justify-content: flex-start;
          flex: 1;

          .footer {
            margin-top: auto;
          }
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
  constructor() {
    this.init();
  }

  async init() {
    const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    const req = postDrafts.getAll();
    req.onsuccess = () => {
      this.drafts = req.result;
      this.cdr.markForCheck();
    };
  }
}
