import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { RolesList } from '../../components/roles/roles-list/roles-list.component';

@Component({
  templateUrl: 'admin-page.html',
  styleUrl: 'admin-page.css',
  imports: [MatTabsModule, MatIconModule, RolesList],
})
export class AdminPage {}
