import { Component, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { ReportsList } from '../../../components/reports-list/reports-list.component';
import { RolesList } from '../../../components/roles/roles-list/roles-list.component';
import { UserService } from '../../../services/user.service';
import { NgComponentOutlet } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

const TABS = [
  { permission: 'v1:roles:read', icon: 'person_shield', label: 'Roles', component: RolesList },
  { permission: 'v1:reports:read', icon: 'flag', label: 'Reports', component: ReportsList },
];

export function hasPanelAccess(permissions: string[]): boolean {
  return TABS.some((t) => permissions.includes(t.permission));
}

@Component({
  templateUrl: 'panel-page.html',
  styleUrl: 'panel-page.css',
  imports: [MatTabsModule, MatIconModule, NgComponentOutlet],
})
export class PanelPage {
  readonly user = inject(UserService).user;
  readonly route = inject(ActivatedRoute);

  readonly tabs = TABS.filter((t) => this.user.permissions.includes(t.permission));
  readonly tabParam = this.route.snapshot.queryParamMap.get('tab') ?? 0;

  selectedIndex = signal(this.tabParam);
  changeTab(index: number) {
    const url = new URL(location.href);
    url.searchParams.set('tab', index.toString());
    history.replaceState({}, '', url);
  }

  hasPermission(permission: string): boolean {
    return this.user.permissions.includes(permission);
  }
}
