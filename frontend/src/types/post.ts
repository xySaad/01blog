import { Hydrator } from './api';
import { User } from './user';

export class Post implements Hydrator {
  id = '';
  account = '';
  owner = new User();
  title = 'Untitled';
  content = '';
  likesCount = 0;
  commentsCount = 0;
  createdAt = new Date(0);
  updatedAt = new Date(0);

  isPublic = false;
  deleted = false;

  get visibility(): 'public' | 'private' | 'draft' {
    return this.isPublic ? 'public' : 'private';
  }

  hydrate() {
    this.createdAt = new Date(this.createdAt);
    this.updatedAt = new Date(this.updatedAt);
  }
}
