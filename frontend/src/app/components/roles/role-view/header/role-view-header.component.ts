import { Component, input, output } from '@angular/core';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { API } from '../../../../lib/api';
import { Role } from '../../../../../types/role';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'role-view-header',
  styles: ':host{display: contents;}',
  templateUrl: 'role-view-header.html',
  imports: [MatExpansionModule, MatIcon, MatButtonModule],
})
export class RoleViewHeader {
  canEdit = input<boolean>(true);
  edit = output<number>();
  role = input.required<Role>();

  delete(id: number) {
    API.delete(`/moderation/roles/${id}`);
  }
}
