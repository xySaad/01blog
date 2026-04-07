import { A11yModule } from '@angular/cdk/a11y';
import { Component, inject, input, output } from '@angular/core';
import { MatAnchor, MatIconButton } from '@angular/material/button';
import {
  MatCardAvatar,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle,
} from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { Router, RouterLink } from '@angular/router';
import { User } from '../../../types/user';
import { API } from '../../lib/api';
import { UserService } from '../../services/user.service';
import { AuditActionMenu } from '../audit-action-menu/audit-action-menu.component';
import { Auditable, AuditAction } from '../../../types/Report';
import { Visibility } from '../../../types/post';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
  selector: 'user-header',
  templateUrl: 'header.html',
  styleUrl: 'header.css',

  imports: [
    MatCardHeader,
    MatIcon,
    MatCardTitle,
    MatCardSubtitle,
    MatCardAvatar,
    MatIconButton,
    MatMenu,
    MatMenuTrigger,
    MatMenuItem,
    A11yModule,
    RouterLink,
    MatAnchor,
    AuditActionMenu,
    MatTooltip,
  ],
})
export class UserHeader {
  owner = input.required<User>();
  material = input.required<{ type: Auditable; id: string }>();
  deleted = input(false);
  createdAt = input<Date>();
  actions = input.required<AuditAction[]>();
  visibility = input<Visibility>();
  Visibility = Visibility;
  edit = output<void>();
  report = output<void>();
  me = inject(UserService).user;
  router = inject(Router);

  visitProfile() {
    this.router.navigateByUrl(`/users/${this.owner().accountId}`);
  }
  //TODO: report API
  toggleFollow() {
    const followed = this.owner().followed;
    this.owner().followed = !followed;

    const method = followed ? 'delete' : 'post';
    API[method](`/follow/${this.owner().accountId}`, null);
  }
}
