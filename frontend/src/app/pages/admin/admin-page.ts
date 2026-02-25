import { Component, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTabChangeEvent, MatTabsModule } from '@angular/material/tabs';
import {
  ActivatedRoute,
  defaultUrlMatcher,
  Route,
  Router,
  UrlSegment,
  UrlSegmentGroup,
} from '@angular/router';
import { MatTabKeyed } from '../../components/mat-tab-keyed.component';
import { ReportsList } from '../../components/reports-list/reports-list.component';
import { RolesList } from '../../components/roles/roles-list/roles-list.component';
import { MatTabGroupKeyed } from '../../directives/mat-tab-group-keyed.directive';

enum AdminPageTab {
  Roles = 'roles',
  Reports = 'reports',
}

const isValidTab = (x: string) => Object.values(AdminPageTab).includes(x as AdminPageTab);

@Component({
  templateUrl: 'admin-page.html',
  styleUrl: 'admin-page.css',
  imports: [MatTabsModule, MatIconModule, RolesList, ReportsList, MatTabKeyed, MatTabGroupKeyed],
})
export class AdminPage {
  readonly AdminPageTab = AdminPageTab;
  static matcher(path: string) {
    return (segments: UrlSegment[], group: UrlSegmentGroup, route: Route) => {
      const result = defaultUrlMatcher(segments, group, { ...route, path });
      const tab = result?.posParams?.['tab']?.path;
      if (tab && isValidTab(tab)) return result;
      return null;
    };
  }

  private router = inject(Router);
  private activeRoute = inject(ActivatedRoute);
  activeTab?: AdminPageTab;
  constructor() {
    console.log('AdminPage constructor');

    const tab = this.activeRoute.snapshot.paramMap.get('tab');
    if (!tab) return;
    if (isValidTab(tab)) this.activeTab = tab as any;
    else throw new Error('tab not found');
  }

  onTabChange(event: MatTabChangeEvent) {
    if (event.tab instanceof MatTabKeyed) {
      const tab: AdminPageTab = event.tab.tabKey();
      this.router.navigate(['/admin', tab]);
    }
  }
}
