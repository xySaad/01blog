import { Hydrator } from './api';
import { User } from './user';

export enum Visibility {
  DELETED,
  HIDDEN,
  PUBLIC,
  PRIVATE,
  DRAFT,
}
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
  liked = false;
  hidden = false;
  isPublic = false;
  deleted = false;

  get visibility(): Visibility {
    return this.deleted
      ? Visibility.DELETED
      : this.hidden
        ? Visibility.HIDDEN
        : this.isPublic
          ? Visibility.PUBLIC
          : Visibility.PRIVATE;
  }

  hydrate() {
    this.createdAt = new Date(this.createdAt);
    this.updatedAt = new Date(this.updatedAt);
  }
}

export class DraftPost extends Post {
  draft = true;
  override get visibility(): Visibility {
    return Visibility.DRAFT;
  }
}
