import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';
import { global } from '../../lib/global';
import { Collection } from '../../../types/collection';
import { Types } from '../../../types';
import { PostCard } from '../../components/post-card/post-card.component';
@Component({
  templateUrl: 'home.html',
  imports: [MatButtonModule, MatIcon, PostCard],
  styles: [
    `
      .home {
        display: flex;
        flex-direction: column;
        gap: 20px;
      }
      .header {
        display: flex;
        gap: 10px;
        width: 100%;

        button {
          flex: 1;
        }
      }
    `,
  ],
})
export class Home {
  router = inject(Router);
  posts = signal<Types.Post[]>([]);

  constructor() {
    global.api.getJson(Collection(Types.Post), '/posts').then(this.posts.set);
  }
}
