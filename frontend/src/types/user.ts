import { Hydrator } from './api';
import { Post } from './post';

export class User {
  accountId = '';
  firstName = '';
  lastName = '';
  login = '';
}

export class UserExtra extends User implements Hydrator {
  posts: Post[] = [];

  hydrate() {
    this.posts.forEach((p) => p.hydrate());
  }
}
