import { Component, computed, input } from '@angular/core';
import { MatCardContent } from '@angular/material/card';
import { MarkdownComponent } from 'ngx-markdown';

@Component({
  selector: 'post-content',
  templateUrl: 'content.html',
  styleUrl: 'content.css',

  imports: [MatCardContent, MarkdownComponent],
})
export class PostCardContent {
  title = input.required<string>();
  content = input.required<string>();
}
