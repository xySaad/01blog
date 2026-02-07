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
import { Types } from '../../../../types';
import { Post } from '../../../../types/post';
import { API } from '../../../lib/api';
import { WhileState } from '../../../lib/decorators/loading';
import { DB_NAME, Storage } from '../../../services/storage.service';
import { AttachmentsDialog } from './attachments-dialog/attachments-dialog';
import { DeleteDialog } from './delete-dialog/delete-dialog.component';

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
  ],
})
export class PostEdit {
  private readonly activeRoute = inject(ActivatedRoute);
  private readonly db = inject(Storage);
  private readonly router = inject(Router);

  params = {
    isNew: false,
    isDraft: false,
  };

  postData = signal(new Types.Post());
  savedData = { title: '', content: '' };
  states = {
    isLoading: signal(false),
    isDraftSaved: computed(
      () =>
        this.savedData.title.trim() === this.postData().title.trim() &&
        this.savedData.content.trim() === this.postData().content.trim(),
    ),
  };

  constructor() {
    this.params.isNew = this.activeRoute.snapshot.queryParamMap.get('new') === 'true';
    this.params.isDraft = this.activeRoute.snapshot.queryParamMap.get('draft') === 'true';
    const id = this.activeRoute.snapshot.paramMap.get('id');
    if (!id) throw new Error('should provide id param');
    this.postData().id = id;
    this.init();
  }

  private async init() {
    if (this.params.isDraft) {
      this.queryLocalDraft();
      return;
    }
    const post = await API.getH(Post, '/posts/' + this.postData().id);
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
    this.postData.update((prev) => {
      prev.title = target.value;
      return prev;
    });
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
    this.postData.update((prev) => {
      prev.content = target.value;
      return prev;
    });
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
  attachments() {
    this.dialog.open(AttachmentsDialog, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
      data: { id: this.postData().id },
    });
  }

  @WhileState((self) => self.states.isLoading)
  async save() {
    const pathId = this.params.isNew ? '' : this.postData().id;
    try {
      const post = await API.postH(Post, `/posts/${pathId}`, this.postData());
      if (this.params.isNew) {
        const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
        postDrafts.delete(this.postData().id);
      }
      this.postData.update((prev) => Object.assign(prev, post));
      this.savedData = this.postData();

      //TODO: remove manual state manupilation
      this.params.isNew = false;
      history.replaceState({}, '', `/posts/edit/${this.postData().id}?isNew=false`);
    } catch (error) {
      //TODO: show error modal
    }
  }

  preview() {
    this.router.navigate(['/posts', this.postData().id], { queryParamsHandling: 'preserve' });
  }
}
