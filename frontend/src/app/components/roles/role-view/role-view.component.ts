import { Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { Permission } from '../../../../types/role';
@Component({
  selector: 'role-view',
  templateUrl: 'role-view.html',
  styleUrl: 'role-view.css',
  imports: [MatExpansionModule, MatCard, MatCardContent, MatButtonModule],
})
export class RoleView {
  permissions = input.required<Permission[]>();
}
