import { Component, computed, effect, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  MatAutocomplete,
  MatAutocompleteTrigger,
  MatOption,
  MatOptgroup,
} from '@angular/material/autocomplete';
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { Permission, Role } from '../../../../types/role';
import { API } from '../../../lib/api';
import { MatAnchor } from '@angular/material/button';

@Component({
  selector: 'create-role',
  templateUrl: 'create-role.html',
  styleUrl: 'create-role.css',
  imports: [
    MatExpansionModule,
    MatIcon,
    MatFormField,
    MatLabel,
    MatChip,
    MatChipSet,
    MatAutocomplete,
    MatOption,
    MatAutocompleteTrigger,
    MatInput,
    MatChipRemove,
    FormsModule,
    MatTooltip,
    MatAnchor,
    MatOptgroup,
  ],
})
export class CreateRole {
  initialRole = input<Role>();
  roles = input.required<Role[]>();
  cancel = output();

  permInput = '';
  nameInput = '';
  descriptionInput = '';

  constructor() {
    effect(() => {
      const initialRole = this.initialRole();
      if (initialRole) {
        this.nameInput = initialRole.name;
        this.descriptionInput = initialRole.description;
        this.selectedPermissions.set(initialRole.permissions);
      }
    });
  }

  permissions = computed(() => {
    const roles = this.roles();
    const allPermissions = roles.flatMap((r) => r.permissions);
    const seen = new Set<number>();
    const selectedIds = new Set(this.selectedPermissions().map((p) => p.id));

    const unassigned: Permission[] = [];
    const assigned: Permission[] = [];

    for (const perm of allPermissions) {
      if (seen.has(perm.id)) continue;
      seen.add(perm.id);

      if (!perm.scope.includes(this.permInput)) continue;

      if (selectedIds.has(perm.id)) assigned.push(perm);
      else unassigned.push(perm);
    }

    return [
      { label: 'Available', perms: unassigned },
      { label: 'Assigned', perms: assigned },
    ];
  });

  selectedPermissions = signal<Permission[]>([]);
  select(added: Permission) {
    const oldPerms = this.selectedPermissions();
    const seen = new Set<number>();
    const newPerms = [...oldPerms, added].filter((perm) => {
      if (seen.has(perm.id)) return false;
      seen.add(perm.id);
      return true;
    });

    this.selectedPermissions.set(Array.from(newPerms));
    this.permInput = '';
  }
  remove(removed: Permission) {
    this.selectedPermissions.update((prev) => prev.filter((perm) => perm.id !== removed.id));
  }

  createOrUpdate() {
    const createdRole: Partial<Role> = {
      id: this.initialRole()?.id,
      name: this.nameInput,
      description: this.descriptionInput,
      permissions: this.selectedPermissions(),
    };

    API.post('/moderation/roles', createdRole);
  }
}
