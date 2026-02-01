import { Component, computed, input } from '@angular/core';
import { Types } from '../../../types';
import { MarkdownComponent } from 'ngx-markdown';
import DOMPurify from 'dompurify';
import { MatIcon } from '@angular/material/icon';
import { MatAnchor } from '@angular/material/button';
import { global } from '../../lib/global';

@Component({
  selector: 'post-view',
  templateUrl: 'post-view.html',
  styleUrl: 'post-view.css',
  imports: [MarkdownComponent, MatIcon, MatAnchor],
})
export class PostView {
  postData = input.required<Types.Post>();
  sanitizedPostContent = computed(() => DOMPurify.sanitize(this.postData().content));
  liked = false; //TODO: fetch self like

  like() {
    this.postData().likesCount += this.liked ? -1 : 1;
    let method: 'post' | 'delete' = this.liked ? 'delete' : 'post';
    global.api[method](`/posts/${this.postData().id}/likes`, '');
    this.liked = !this.liked;
  }
}
