import { Component, inject, signal } from '@angular/core';
import { Types } from '../../../types';
import { Collection } from '../../../types/collection';
import { DraftPost, Post } from '../../../types/post';
import { PostCard } from '../../components/post-card/post-card.component';
import { API } from '../../lib/api';
import { DB_NAME, Storage } from '../../services/storage.service';
import { UserService } from '../../services/user.service';
@Component({
  templateUrl: 'user-posts.html',
  styleUrl: 'user-posts.css',
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
  private db = inject(Storage);
  protected syncedPosts = signal<Post[]>([]);
  protected draftposts = signal<Post[]>([]);
  selfUser = inject(UserService).user;
  constructor() {
    this.init();
  }

  async init() {
    const syncedPosts = await API.getH(
      Collection(Types.Post),
      `/users/${this.selfUser.accountId}/posts`,
    );
    this.syncedPosts.set(syncedPosts);

    const syncedPostsMap = new Map<string, Post>();
    syncedPosts.forEach((sp) => syncedPostsMap.set(sp.id, sp));

    const localPosts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    const req: IDBRequest<Types.Post[]> = localPosts.getAll();
    const { promise, resolve, reject } = Promise.withResolvers();
    req.onsuccess = resolve;
    req.onerror = reject;
    await promise;

    const draftPosts = req.result.filter((lp) => {
      Object.setPrototypeOf(lp, DraftPost.prototype);
      return !syncedPostsMap.has(lp.id);
    });
    console.log(draftPosts);

    this.draftposts.set(draftPosts);
  }
}
