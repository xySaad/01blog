import { Component, inject, signal, WritableSignal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardFooter, MatCardHeader } from '@angular/material/card';
import { MatRipple } from '@angular/material/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { API } from '../../../../lib/api';

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
    const urls: string[] = await API.get(`/posts/${this.data.id}/media`);
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
    const headers = {
      'X-File-Name': encodeURIComponent(fNameNoExtension),
      'Content-Type': 'application/octet-stream',
    };
    const url: string = await API.postRaw(`/posts/${this.data.id}/media`, blob, headers);
    file.url = url;
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
