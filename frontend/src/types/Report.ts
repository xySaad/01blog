import { enumString } from '../app/lib/enum';
import { snake2StartCase } from '../app/lib/fmt';
import { Hydrator } from './api';

export type Report = {
  POST: PostReport;
  COMMENT: CommentReport;
  USER: UserReport;
  OTHER: ReportModel;
};
export const ByDefault = {
  id: '',
  login: '',
};
export type Action = keyof typeof Action;
export const Action = enumString('BAN_USER', 'DELETE_CONTENT', 'IGNORE_REPORT');

export class ReportModel implements Hydrator {
  readonly type: keyof Report = 'OTHER';
  id = '';
  reason = '';
  description = '';
  createdAt = new Date();
  reportedBy = ByDefault;
  resolvedBy? = ByDefault;
  fmtReason = '';
  actionTaken?: keyof typeof Action;

  hydrate() {
    this.createdAt = new Date(this.createdAt);
    this.fmtReason = snake2StartCase(this.reason);
  }

  is<T extends keyof Report>(target: T): this is Report[T] {
    return this.type === target;
  }
}

export class PostReport extends ReportModel {
  override readonly type = 'POST';
  postId = '';
  postTitle = '';
}

export class CommentReport extends ReportModel {
  override readonly type = 'COMMENT';
  commentId = '';
  commentContent = '';
  postId = '';
  postTitle = '';
}

export class UserReport extends ReportModel {
  override readonly type = 'USER';
  userId = '';
  userLogin = '';
}
