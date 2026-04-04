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
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { Action, ReportModel } from '../../../types/Report';
import { API } from '../../lib/api';
import { enumValues as enumValues } from '../../lib/enum';
import { snake2StartCase } from '../../lib/fmt';

//actions and their icons
const Icons: Record<keyof typeof Action, string> = {
  BAN_USER: 'block',
  DELETE_CONTENT: 'delete',
  IGNORE_REPORT: 'close',
};

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
    MatMenuTrigger,
    MatMenuItem,
    MatMenu,
    MatTooltip,
    MatCardFooter,
  ],
})
//TODO: fetch actions from server
export class ReportCard extends MatCard {
  report = input.required<ReportModel>();
  title = input.required<string>();
  iconName = input<string>();
  iconTitle = input<string>();
  materialPath = input<string>();
  router = inject(Router);

  snake2StartCase = snake2StartCase;
  actions = enumValues(Action);
  Icons = Icons;

  takeAction(action: Action) {
    const { id } = this.report();
    return API.post('/moderation/audit', { id, action });
  }

  review() {
    const { id } = this.report();
    const path = this.materialPath();
    if (!path) return;
    console.log('navigating to:', `/moderation/reports/${id}/${path}`);

    this.router.navigateByUrl(`/moderation/reports/${id}/${path}`);
  }
}
