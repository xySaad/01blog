import { Component, inject, input } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardFooter,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle,
} from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { AuditAction, Report, Auditable } from '../../../types/Report';
import { AuditService } from '../../services/audit-service.service';
import { AuditActionMenu } from '../audit-action-menu/audit-action-menu.component';
import { API } from '../../lib/api';

@Component({
  selector: 'report-card',
  templateUrl: 'report-card.html',
  styleUrl: 'report-card.css',
  imports: [
    MatIcon,
    MatCardHeader,
    MatCardTitle,
    MatCardSubtitle,
    MatCardContent,
    MatCardActions,
    MatAnchor,
    MatTooltip,
    MatCardFooter,
    AuditActionMenu,
  ],
})
export class ReportCard extends MatCard {
  report = input.required<Report>();
  title = input.required<string>();
  iconName = input<string>();
  iconTitle = input<string>();
  materialPath = input<string>();
  router = inject(Router);

  auditService = inject(AuditService);

  actions(type: Auditable) {
    let base: AuditAction[] = ['BAN_USER', 'DELETE'];
    if (type === Auditable.POST) base = ['HIDE', ...base];
    return base;
  }
  async ignore() {
    const { id } = this.report();
    await API.post(`/moderation/audit/report/${id}/ignore`, null);
  }
  review() {
    const { id } = this.report();
    const path = this.materialPath();
    if (!path) return;
    console.log('navigating to:', `/moderation/reports/${id}/${path}`);

    this.router.navigateByUrl(`/moderation/reports/${id}/${path}`);
  }
}
