import { Component, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { Types } from '../../../types';
import { Collection } from '../../../types/collection';
import { PostCard } from '../../components/post-card/post-card.component';
import { API } from '../../lib/api';
@Component({
  templateUrl: 'home.html',
  styleUrl: 'home.css',
  imports: [MatButtonModule, PostCard],
})
export class Home {
  posts = signal<Types.Post[]>([]);

  constructor() {
    API.getH(Collection(Types.Post), '/posts').then(this.posts.set);
  }
}
