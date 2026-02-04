import { Component, input, output } from '@angular/core';
import {
  MatCardHeader,
  MatCardTitle,
  MatCardSubtitle,
  MatCardAvatar,
} from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { User } from '../../../types/user';
import { MatIconButton } from '@angular/material/button';
import { global } from '../../lib/global';
import { MatMenu, MatMenuTrigger, MatMenuItem } from '@angular/material/menu';
import { A11yModule } from '@angular/cdk/a11y';

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
  ],
})
export class UserHeader {
  owner = input.required<User>();
  createdAt = input.required<Date>();
  edit = output<void>();

  me = global.user;

  //TODO: report API
  report() {}
}
