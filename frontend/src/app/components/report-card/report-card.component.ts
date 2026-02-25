import { Component, input, output } from '@angular/core';
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
import { ReportModel } from '../../../types/Report';
import { API } from '../../lib/api';
import { snake2StartCase } from '../../lib/fmt';
//actions and their icons
const ActionIcons = {
  BAN_USER: 'block',
  DELETE_CONTENT: 'delete',
  IGNORE: 'close',
} as const;
type Actions = keyof typeof ActionIcons;

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
  ActionIcons = ActionIcons;
  snake2StartCase = snake2StartCase;
  actions = input<Actions[]>(Object.keys(ActionIcons) as Actions[]);
  review = output();
  takeAction(action: Actions) {
    const { id } = this.report();
    return API.post('/moderation/audit', { id, action });
  }
}
