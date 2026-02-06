import { Convert } from '../app/lib/decorators/type';
import { User } from './user';

export class Post {
  id = '';
  account = '';
  owner = new User();
  title = 'Untitled';
  content = '';
  likesCount = 0;
  commentsCount = 0;
  @Convert createdAt: Date = new Date(0);
  @Convert updatedAt: Date = new Date(0);

  isPublic = false;
  deleted = false;

  get visibility(): 'public' | 'private' | 'draft' {
    return this.isPublic ? 'public' : 'private';
  }
}
