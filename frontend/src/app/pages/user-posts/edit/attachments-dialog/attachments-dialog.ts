import {
  ChangeDetectorRef,
  Component,
  inject,
  signal,
  Signal,
  WritableSignal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogContent,
  MatDialogActions,
  MatDialogTitle,
  MatDialogClose,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatCard, MatCardContent, MatCardFooter, MatCardHeader } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRipple } from '@angular/material/core';
import { global } from '../../../../lib/global';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Collection } from '../../../../../types/collection';

type FilePreview = { url: string; name: string; loading: WritableSignal<boolean> };

@Component({
  selector: 'attachments-dialog',
  templateUrl: 'attachments-dialog.html',
  styleUrl: 'attachments-dialog.css',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButtonModule,
    MatDialogClose,
    MatCard,
    MatGridListModule,
    MatIcon,
    MatCardContent,
    MatCardFooter,
    MatProgressSpinner,
    MatRipple,
    MatCardHeader,
    MatFormFieldModule,
  ],
})
export class AttachmentsDialog {
  data: { id: string } = inject(MAT_DIALOG_DATA);

  postFiles = null;
  previewFiles: WritableSignal<FilePreview[]> = signal([]);

  constructor() {
    this.init();
    //TODO: fetch post files
  }
  async init() {
    const urls = await global.api.getJson(Collection<string>(), `/posts/${this.data.id}/media`);
    const files = urls.map((url) => {
      const idx = url.indexOf(this.data.id) + this.data.id.length + 1;
      return {
        name: decodeURIComponent(url.substring(idx)),
        url,
        loading: signal(false),
      };
    });
    this.previewFiles.update((prev) => [...prev, ...files]);
  }
  async handleFileUpload(target: HTMLInputElement) {
    if (!target.files) return;
    const targetFiles: File[] = target.files as any;

    for (const file of targetFiles) {
      const url = await this.blob2b64(file);
      const previewFile = { name: file.name, url, loading: signal(true) };
      this.previewFiles.update((prev) => [...prev, previewFile]);
      this.uploadFile(previewFile, file);
    }
  }

  async uploadFile(file: FilePreview, blob: Blob) {
    const fNameNoExtension = file.name.substring(0, file.name.lastIndexOf('.'));

    const resp = await global.api.post(`/posts/${this.data.id}/media`, blob, {
      'X-File-Name': encodeURIComponent(fNameNoExtension),
    });
    if (resp.ok) {
      //TODO: handle error
    }
    file.url = await resp.text();
    file.loading.set(false);
  }
  async blob2b64(blob: Blob) {
    const { promise, resolve } = Promise.withResolvers();
    const reader = new FileReader();
    reader.onload = resolve;
    reader.readAsDataURL(blob);
    await promise;
    return reader.result as string;
  }

  copyMarkdown(file: FilePreview) {
    const markdown = `![${file.name}](${file.url})`;

    navigator.clipboard.writeText(markdown).then(() => {
      console.log('Markdown copied to clipboard');
      //TODO: add snackbar
    });
  }
}
