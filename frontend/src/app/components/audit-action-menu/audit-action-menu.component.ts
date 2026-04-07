import { Component, inject, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuTrigger, MatMenuItem } from '@angular/material/menu';
import { Auditable, AuditAction } from '../../../types/Report';
import { API } from '../../lib/api';
import { snake2StartCase } from '../../lib/fmt';
import { AuditService } from '../../services/audit-service.service';
import { MatAnchor } from '@angular/material/button';

@Component({
  selector: 'audit-action-menu',
  templateUrl: 'audit-action-menu.html',
  imports: [MatMenu, MatIcon, MatMenuTrigger, MatAnchor, MatMenuItem],
})
export class AuditActionMenu {
  material = input.required<{ reportId: string } | { type: Auditable; id: string }>();
  disabled = input();
  asMenuItem = input(false);
  actions = input.required<AuditAction[]>();
  auditService = inject(AuditService);
  snake2StartCase = snake2StartCase;

  async takeAction(action: AuditAction) {
    const material = this.material();
    if ('reportId' in material) {
      await API.post('/moderation/audit/report', { id: material.reportId, action });
    } else {
      await API.post('/moderation/audit/content', { material, action });
    }
  }
}
