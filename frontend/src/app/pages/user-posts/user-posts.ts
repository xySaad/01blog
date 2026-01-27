import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { DB_NAME, Storage } from '../../services/storage.service';
import { Router } from '@angular/router';
import { global } from '../../lib/global';
import { PostCard } from '../../components/post-card/post-card.component';
import { Types } from '../../../types';
import { Collection } from '../../../types/collection';
@Component({
  templateUrl: 'user-posts.html',
  imports: [PostCard],
  providers: [
    Storage,
    {
      provide: DB_NAME,
      useValue: 'blog',
    },
  ],
})
export class PostsList {
  router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private db = inject(Storage);

  localDrafts: Types.Post[] = [];
  savedPosts: Types.Post[] = [];
  localUnsavedDrafts: Types.Post[] = [];
  constructor() {
    this.init();
  }

  async init() {
    const posts = await global.api.getJson(
      Collection(Types.Post),
      `/user/${global.user?.accountId}/posts`,
    );
    this.savedPosts = posts;

    const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    const req: IDBRequest<Types.Post[]> = postDrafts.getAll();
    req.onsuccess = () => {
      req.result.forEach((draft) => {
        const savedPost = this.savedPosts.find((savedPost) => savedPost.id === draft.id);
        if (savedPost) {
          if (savedPost.updatedAt.getTime() < draft.updatedAt.getTime()) {
            this.localDrafts.push(draft);
          }
        } else {
          this.localUnsavedDrafts.push(draft);
        }
      });
      this.cdr.markForCheck();
    };
  }
}
