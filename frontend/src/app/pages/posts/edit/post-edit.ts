import { Component, computed, inject, Signal, signal, Type } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { TextFieldModule } from '@angular/cdk/text-field';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MarkdownComponent } from 'ngx-markdown';
import { FormsModule } from '@angular/forms';
import DOMPurify from 'dompurify';
import { MatToolbar } from '@angular/material/toolbar';
import { DB_NAME, Storage } from '../../../services/storage.service';
import { ActivatedRoute } from '@angular/router';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialog } from '@angular/material/dialog';
import { DeleteDialog } from './delete-dialog/delete-dialog.component';
import { global } from '../../../lib/global';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Post } from '../../../../types/post';
import { WhileState } from '../../../lib/decorators/loading';
import { Types } from '../../../../types';

@Component({
  selector: 'new-post',
  templateUrl: 'post-edit.html',
  styleUrl: 'post-edit.css',
  //TODO: use account id as DB_NAME value
  providers: [Storage, { provide: DB_NAME, useValue: 'blog' }],
  imports: [
    TextFieldModule,
    MatInputModule,
    MatFormFieldModule,
    MatIcon,
    MarkdownComponent,
    FormsModule,
    MatToolbar,
    MatProgressBarModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatTooltipModule,
  ],
})
export class PostEdit {
  private readonly route = inject(ActivatedRoute);
  private readonly db = inject(Storage);
  params = {
    isNew: false,
    isDraft: false,
  };

  postData = signal(new Types.Post());
  sanitizedPostContent = computed(() => DOMPurify.sanitize(this.postData().content));
  savedData = { title: '', content: '' };
  states = {
    isOnPreview: true,
    isLoading: signal(false),
    isDraftSaved: computed(
      () =>
        this.savedData.title.trim() === this.postData().title.trim() &&
        this.savedData.content.trim() === this.postData().content.trim(),
    ),
  };

  constructor() {
    this.params.isNew = this.route.snapshot.queryParamMap.get('new') === 'true';
    this.params.isDraft = this.route.snapshot.queryParamMap.get('draft') === 'true';
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) throw new Error('should provide id param');
    this.postData().id = id;
    this.init();
  }

  private async init() {
    if (this.params.isDraft) {
      this.queryLocalDraft();
      return;
    }
    const post = await global.api.getJson(Post, '/posts/' + this.postData().id);
    this.postData.set(post);
    this.savedData = this.postData();
  }
  async queryLocalDraft() {
    const store = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    const req = store.get(this.postData().id);
    req.onsuccess = () => {
      if (!req.result) return;
      this.postData.set(req.result);
    };
  }
  async titleInput(target: any) {
    this.postData.update((prev) => ({ ...prev, title: target.value }));
    await this.updateLocalDraft();
  }

  private adjustScrollbar() {
    const scrollHeight = document.documentElement.scrollHeight;
    const diff = scrollHeight - this.lastHeight;
    window.scrollBy({ top: diff, left: 0 });
    this.lastHeight = scrollHeight;
  }

  private lastHeight = 0;
  async textInput(target: any) {
    this.adjustScrollbar();
    this.postData.update((prev) => ({ ...prev, content: target.value }));
    await this.updateLocalDraft();
  }

  async updateLocalDraft(force = false) {
    if (!force && Date.now() - this.postData().updatedAt.getTime() < 5000) return;
    console.debug('auto saving local draft');

    this.postData().updatedAt = new Date();
    const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    postDrafts.put(this.postData(), this.postData().id);
  }

  private readonly dialog = inject(MatDialog);
  delete() {
    this.dialog.open(DeleteDialog, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
      data: { id: this.postData().id, isDraft: this.params.isDraft },
    });
  }

  @WhileState((self) => self.states.isLoading)
  async save() {
    const pathId = this.params.isNew ? '' : this.postData().id;
    try {
      const post = await global.api.postJson(
        Post,
        `/posts/${pathId}`,
        JSON.stringify(this.postData()),
      );
      if (this.params.isNew) {
        const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
        postDrafts.delete(this.postData().id);
      }
      this.postData.update((prev) => ({ ...prev, ...post }));
      this.savedData = this.postData();

      //TODO: remove manual state manupilation
      this.params.isNew = false;
      history.replaceState({}, '', `/posts/edit/${this.postData().id}?isNew=false`);
    } catch (error) {
      //TODO: show error modal
    }
  }
}
