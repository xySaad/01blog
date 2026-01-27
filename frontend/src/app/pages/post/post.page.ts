import { Component, inject, Signal, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { global } from '../../lib/global';
import { Types } from '../../../types';
import { PostView } from '../../components/post-view/post-view';

@Component({
  templateUrl: 'post.page.html',
  imports: [PostView],
})
export class PostPage {
  private readonly route = inject(ActivatedRoute);
  postData = signal(new Types.Post());

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) throw new Error('should provide id param');

    const post = global.api.getJson(Types.Post, '/posts/' + id);
    post.then(this.postData.set);
  }
}
