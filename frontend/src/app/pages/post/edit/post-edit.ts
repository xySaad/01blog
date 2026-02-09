import { TextFieldModule } from '@angular/cdk/text-field';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatToolbar } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';
import { Post } from '../../../../types/post';
import { API } from '../../../lib/api';
import { WhileState } from '../../../lib/decorators/loading';
import { DB_NAME, Storage } from '../../../services/storage.service';
import { AttachmentsDialog } from './attachments-dialog/attachments-dialog';
import { DeleteDialog } from './delete-dialog/delete-dialog.component';
import { betterSignal } from '../../../lib/signal';
import { PostCardContent } from '../../../components/post-view/content/content.component';
import { global } from '../../../lib/global';

@Component({
  selector: 'post-edit',
  templateUrl: 'post-edit.html',
  styleUrl: 'post-edit.css',
  //TODO: use account id as DB_NAME value
  providers: [Storage, { provide: DB_NAME, useValue: 'blog' }],
  imports: [
    TextFieldModule,
    MatInputModule,
    MatFormFieldModule,
    MatIcon,
    FormsModule,
    MatToolbar,
    MatProgressBarModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatTooltipModule,
    PostCardContent,
  ],
})
export class PostEdit {
  private readonly activeRoute = inject(ActivatedRoute);
  private readonly db = inject(Storage);
  private readonly router = inject(Router);

  syncedPostData = signal(new Post());
  localPostData = betterSignal(new Post());
  states = {
    isPreviewActive: signal(false),
    isNew: false,
    currentlySyncing: signal(false),
    isLoading: signal(false),
    isSynced: computed(() => {
      const local = this.localPostData();
      const synced = this.syncedPostData();
      const titleChanged = local.title.trim() === synced.title.trim();
      const contentChanged = local.content.trim() === synced.content.trim();

      return titleChanged && contentChanged;
    }),
  };

  constructor() {
    const id = this.activeRoute.snapshot.paramMap.get('id');
    if (!id) throw new Error('should provide id param');
    this.init(id);
  }

  //TODO: cleanup this ugly code
  private async init(postId: string) {
    const storedLocalPost = await this.queryLocalDraft(postId);
    try {
      const syncedPost = await API.getH(Post, `/posts/${postId}`);
      this.syncedPostData.set(syncedPost);
      const localPostData = Object.assign(new Post(), storedLocalPost ?? syncedPost);
      this.localPostData.set(localPostData);
    } catch (error) {
      this.states.isNew = true;
      const localPostData = Object.assign(
        new Post(),
        { id: postId, owner: global.user, account: global.user.accountId },
        storedLocalPost,
      );

      this.localPostData.set(localPostData);
    }
  }

  async queryLocalDraft(postId: string) {
    const store = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    const req = store.get(postId);
    const { resolve, promise, reject } = Promise.withResolvers();
    req.onsuccess = resolve;
    req.onerror = reject;
    await promise;
    return req.result as Post | undefined;
  }

  async titleInput(target: any) {
    this.localPostData.patch((prev) => {
      prev.title = target.value;
      return prev;
    });
    await this.updateLocalDraft();
  }

  private lastHeight = 0;
  async textInput(target: any) {
    //adjust scrollbar
    //TODO: use css to limit scrollbar instead of js
    const scrollHeight = document.documentElement.scrollHeight;
    const diff = scrollHeight - this.lastHeight;
    window.scrollBy({ top: diff, left: 0 });
    this.lastHeight = scrollHeight;

    this.localPostData.patch((prev) => {
      prev.content = target.value;
      return prev;
    });
    await this.updateLocalDraft();
  }

  async updateLocalDraft() {
    const post = this.localPostData();
    if (Date.now() - post.updatedAt.getTime() < 5000) return;
    console.debug('auto saving local draft');
    this.states.currentlySyncing.set(true);
    post.updatedAt = new Date();
    const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    postDrafts.put(post, post.id);
    setTimeout(() => {
      this.states.currentlySyncing.set(false);
    }, 1000);
  }

  @WhileState((self) => self.states.isLoading)
  async save() {
    const post = this.localPostData();
    const pathId = this.states.isNew ? '' : post.id;
    try {
      const postUpdate = await API.postH(Post, `/posts/${pathId}`, post);
      const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
      postDrafts.delete(post.id);
      const newPost = Object.assign(post, postUpdate);
      this.syncedPostData.update((prev) => Object.assign(prev, newPost));
      this.localPostData.patch((prev) => Object.assign(prev, newPost));
      if (this.states.isNew) {
        history.replaceState({}, '', `/posts/${newPost.id}`);
        this.states.isNew = false;
      }
    } catch (error) {
      //TODO: show error modal
    }
  }

  togglePreview() {
    this.states.isPreviewActive.update((prev) => !prev);
  }

  private readonly dialog = inject(MatDialog);
  delete() {
    this.dialog.open(DeleteDialog, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
      data: { id: this.localPostData().id, isDraft: this.states.isNew },
    });
  }

  attachments() {
    this.dialog.open(AttachmentsDialog, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
      data: { id: this.localPostData().id },
    });
  }
}
