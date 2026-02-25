import { snake2StartCase } from '../app/lib/fmt';
import { Hydrator } from './api';

export type Report = {
  POST: PostReport;
  COMMENT: CommentReport;
  USER: UserReport;
  OTHER: ReportModel;
};

export class ReportModel implements Hydrator {
  id = '';
  reportedBy = {
    id: '',
    login: '',
  };
  resolvedBy? = {
    id: '',
    login: '',
  };

  reason = '';
  description = '';
  createdAt = new Date();

  readonly type: keyof Report = 'OTHER';

  fmtReason = '';
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
  postId = '';
  postTitle = '';
}

export class UserReport extends ReportModel {
  override readonly type = 'USER';
  userId = '';
  userLogin = '';
}
