import { Injectable } from '@angular/core';
import { enumValues } from '../lib/enum';
import { AuditAction, ReportAction } from '../../types/Report';

@Injectable({ providedIn: 'root' })
export class AuditService {
  //actions and their icons
  ActionIcons: Record<ReportAction, string> = {
    BAN_USER: 'block',
    DELETE: 'delete',
    HIDE: 'visibility_off',
    IGNORE_REPORT: 'check',
  };
  Actions = enumValues(AuditAction);
}
