import { User } from './user';

export class Comment {
  id = '';
  account = '';
  post = '';
  content = '';
  deleted = false;
}

export class CommentExtra extends Comment {
  owner = new User();
}
