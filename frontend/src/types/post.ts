import { Convert } from '../app/lib/decorators/type';

export class Post {
  id = '';
  account = '';
  accountName = '';
  title = 'Untitled';
  content = '';

  @Convert createdAt: Date = new Date(0);
  @Convert updatedAt: Date = new Date(0);

  isPublic = false;
  deleted = false;
}
