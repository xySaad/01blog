import { User } from './user';

export class Comment {
  id = '';
  account = '';
  post = '';
  content = '';
  deleted = false;
}

export class CommentWithUser {
  comment = new Comment();
  user = new User();
}
