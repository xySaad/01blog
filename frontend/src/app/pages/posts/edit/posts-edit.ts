import { ChangeDetectorRef, Component, inject, signal } from '@angular/core';
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

@Component({
  selector: 'new-post',
  templateUrl: 'posts-edit.html',
  providers: [
    Storage,
    {
      provide: DB_NAME,
      useValue: 'blog',
    },
  ],
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
  styleUrl: 'posts-edit.css',
})
export class NewPost {
  readonly dialog = inject(MatDialog);
  private cdr = inject(ChangeDetectorRef);
  private route = inject(ActivatedRoute);
  private db = inject(Storage);
  preview = true;
  loading = signal(false);

  private id;
  title = 'Untitled';
  text = '';
  safe = '';
  savedPost = {
    title: '',
    text: '',
  };
  isSaved = signal(false);

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) throw new Error('should provide id param');
    this.id = id;
    this.init();
  }

  async init() {
    const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    const req = postDrafts.get(this.id);
    req.onsuccess = () => {
      if (!req.result) return;
      this.title = req.result.title;
      this.preview = req.result.preview;
      this.text = req.result.text;
      this.safe = DOMPurify.sanitize(this.text);
      this.cdr.markForCheck();
    };
  }

  public async titleInput(target: any) {
    this.title = target.value;
    await this.updateLocalDraft();
  }

  lastHeight = 0;
  public async textInput(target: any) {
    const scrollHeight = document.documentElement.scrollHeight;
    const diff = scrollHeight - this.lastHeight;
    window.scrollBy({ top: diff, left: 0 });
    this.lastHeight = scrollHeight;

    this.text = target.value;
    this.safe = DOMPurify.sanitize(this.text);
    await this.updateLocalDraft();
  }

  lastTime = Date.now();
  async updateLocalDraft(force = false) {
    if (this.savedPost.title === this.title && this.savedPost.text === this.text) {
      this.isSaved.set(true);
    } else {
      this.isSaved.set(false);
    }

    if (Date.now() - this.lastTime < 5000) return;
    console.log('save');

    this.lastTime = Date.now();

    const postDrafts = await this.db.getOrCreate('post-drafts', 'readwrite', 'id');
    postDrafts.put(
      { title: this.title, text: this.text, preview: this.preview, id: this.id },
      this.id,
    );
  }

  delete() {
    this.dialog.open(DeleteDialog, {
      enterAnimationDuration: '100ms',
      exitAnimationDuration: '100ms',
      data: { id: this.id },
    });
  }

  async save() {
    this.loading.set(true);
    const data = { title: this.title, text: this.text };
    const resp = await global.api.post('/posts/', JSON.stringify(data));
    if (!resp.ok) {
      //TODO: show an error modal
    }
    this.savedPost = data;
    this.updateLocalDraft(true);

    setTimeout(() => {
      this.loading.set(false);
    }, 3000);
  }
}
