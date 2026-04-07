import { enumString, enumValues } from '../app/lib/enum';
import { snake2StartCase } from '../app/lib/fmt';
import { Hydrator } from './api';
import { UserMinimal } from './user';

export type Auditable = keyof typeof Auditable;
export const Auditable = enumString(
  //
  'POST',
  'USER',
  'COMMENT',
);
type ReportTypes = {
  POST: PostReport;
  USER: UserReport;
  COMMENT: CommentReport;
};

export type AuditAction = keyof typeof AuditAction;
export const AuditAction = enumString(
  //
  'BAN_USER',
  'DELETE',
  'HIDE',
);

export type ReportAction = keyof typeof ReportAction;
export const ReportAction = enumString(...enumValues(AuditAction), 'IGNORE_REPORT');
export class Report implements Hydrator {
  material!: {
    type: Auditable;
  };
  id!: string;
  reason!: string;
  description?: string;
  createdAt!: Date;
  reportedBy!: UserMinimal;
  resolvedBy?: UserMinimal;
  actionTaken?: ReportAction;

  fmtReason = '';
  hydrate() {
    this.createdAt = new Date(this.createdAt);
    this.fmtReason = snake2StartCase(this.reason);
  }

  is<T extends Auditable>(target: T): this is ReportTypes[T] {
    return this.material.type === target;
  }
}

export class UserReport extends Report {
  userId!: string;
  userLogin!: string;
}

export class PostReport extends Report {
  postId!: string;
  postTitle!: string;
}

export class CommentReport extends Report {
  postId!: string;
  commentId!: string;
  commentContent!: string;
}
