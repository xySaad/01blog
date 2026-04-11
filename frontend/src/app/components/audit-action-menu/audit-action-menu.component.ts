import { Component, inject, input } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { Auditable, AuditAction, Report } from '../../../types/Report';
import { LoadingSpinner } from '../../directives/loading-spinner.directive';
import { API } from '../../lib/api';
import { snake2StartCase } from '../../lib/fmt';
import { AuditService } from '../../services/audit-service.service';
import { UserService } from '../../services/user.service';
import { ConfirmationDialog } from '../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'audit-action-menu',
  templateUrl: 'audit-action-menu.html',
  imports: [
    MatMenu,
    MatIcon,
    MatMenuTrigger,
    MatMenuItem,
    LoadingSpinner,
    MatExpansionModule,
    MatAnchor,
    ConfirmationDialog,
  ],
})
export class AuditActionMenu {
  material = input.required<Report | { type: Auditable; id: string }>();
  disabled = input();
  actions = input.required<AuditAction[]>();
  auditService = inject(AuditService);
  selfUser = inject(UserService).user;
  snake2StartCase = snake2StartCase;

  async takeAction(action: AuditAction) {
    const material = this.material();

    if (material instanceof Report) {
      await API.post('/moderation/audit/report', { id: material.id, action });
      material.resolvedBy = this.selfUser;
      material.actionTaken = action;
    } else {
      await API.post('/moderation/audit/content', { material, action });
    }
  }
}
