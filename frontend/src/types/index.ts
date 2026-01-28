import { Post as PostClass } from './post';
import { User as UserClass } from './user';
import { Comment as CommentClass } from './comment';
import { CommentWithUser as CommentWithUserClass } from './comment';

export namespace Types {
  export type Post = PostClass;
  export const Post = PostClass;

  export type User = UserClass;
  export const User = UserClass;

  export type Comment = CommentClass;
  export const Comment = CommentClass;

  export type CommentWithUser = CommentWithUserClass;
  export const CommentWithUser = CommentWithUserClass;
}
