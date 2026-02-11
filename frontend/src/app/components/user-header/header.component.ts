import { A11yModule } from '@angular/cdk/a11y';
import { Component, inject, input, output } from '@angular/core';
import { MatIconButton, MatAnchor } from '@angular/material/button';
import {
  MatCardAvatar,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle,
} from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { Router } from '@angular/router';
import { User } from '../../../types/user';
import { global } from '../../lib/global';
import { RouterLink } from '@angular/router';
import { API } from '../../lib/api';

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
  ],
})
export class UserHeader {
  owner = input.required<User>();
  createdAt = input<Date>();
  edit = output<void>();
  report = output<void>();

  me = global.user;
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
