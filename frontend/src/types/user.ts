import { Hydrator } from './api';
import { Post } from './post';

export class User {
  accountId = '';
  firstName = '';
  lastName = '';
  login = '';
  followed = false;
}

export class UserExtra extends User implements Hydrator {
  posts: Post[] = [];
  followersCount = 0;
  followingCount = 0;

  hydrate() {
    this.posts.forEach((p) => Post.prototype.hydrate.call(p));
  }
}
