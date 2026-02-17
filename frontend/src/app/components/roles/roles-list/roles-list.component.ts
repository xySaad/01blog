import { Component, signal } from '@angular/core';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
} from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { Role } from '../../../../types/role';
import { API } from '../../../lib/api';
import { CreateRole } from '../create-role/create-role.component';
import { RoleView } from '../role-view/role-view.component';
import { RoleViewHeader } from '../role-view/header/role-view-header.component';

@Component({
  selector: 'roles-list',
  templateUrl: 'roles-list.html',
  styleUrl: 'roles-list.css',
  imports: [
    RoleView,
    CreateRole,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatIcon,
    RoleViewHeader,
  ],
})
export class RolesList {
  roles = signal<Role[]>([]);
  editIdx = signal(0);

  constructor() {
    this.init();
  }

  async init() {
    const roles = await API.get<Role[]>('/moderation/roles');
    this.roles.set(roles);
  }
  canEdit(role: Role, panel: MatExpansionPanel) {
    const expanded = panel._getExpandedState() === 'expanded';
    const isDefaultRole = role.id === 1;
    return expanded && !isDefaultRole;
  }
}
