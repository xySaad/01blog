import { Component, computed, input, signal } from '@angular/core';
import { Types } from '../../../types';
import { MarkdownComponent } from 'ngx-markdown';
import DOMPurify from 'dompurify';

@Component({
  selector: 'post-view',
  templateUrl: 'post-view.html',
  imports: [MarkdownComponent],
})
export class PostView {
  postData = input.required<Types.Post>();
  sanitizedPostContent = computed(() => DOMPurify.sanitize(this.postData().content));
}
