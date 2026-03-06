import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { ReportsList } from '../../components/reports-list/reports-list.component';
import { RolesList } from '../../components/roles/roles-list/roles-list.component';
import { UserService } from '../../services/user.service';
import { NgComponentOutlet } from '@angular/common';

const TABS = [
  { permission: 'v1:roles:read', icon: 'person_shield', label: 'Roles', component: RolesList },
  { permission: 'v1:reports:read', icon: 'flag', label: 'Reports', component: ReportsList },
];

export function hasAdminAccess(permissions: string[]): boolean {
  return TABS.some((t) => permissions.includes(t.permission));
}

@Component({
  templateUrl: 'admin-page.html',
  styleUrl: 'admin-page.css',
  imports: [MatTabsModule, MatIconModule, NgComponentOutlet],
})
export class AdminPage {
  readonly user = inject(UserService).user;
  readonly tabs = TABS.filter((t) => this.user.permissions.includes(t.permission));

  hasPermission(permission: string): boolean {
    return this.user.permissions.includes(permission);
  }
}
