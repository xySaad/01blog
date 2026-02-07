import { A11yModule } from '@angular/cdk/a11y';
import { Component, inject, input, output } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
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
  ],
})
export class UserHeader {
  owner = input.required<User>();
  createdAt = input<Date>();
  edit = output<void>();

  me = global.user;
  router = inject(Router);

  visitProfile() {
    this.router.navigateByUrl(`/users/${this.owner().accountId}`);
  }
  //TODO: report API
  report() {}
}
