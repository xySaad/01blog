import { Component, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatChip, MatChipSet, MatChipRemove } from '@angular/material/chips';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput } from '@angular/material/input';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { MatRadioButton, MatRadioModule } from '@angular/material/radio';
import { MatTooltip } from '@angular/material/tooltip';
import { Permission } from '../../../../types/role';
import { User } from '../../../../types/user';
import { API } from '../../../lib/api';
import { UserSheet } from '../../user-sheet/user-sheet.component';

@Component({
  selector: 'role-view',
  templateUrl: 'role-view.html',
  styleUrl: 'role-view.css',
  imports: [
    MatExpansionModule,
    MatCard,
    MatCardContent,
    MatButtonModule,
    MatIcon,
    MatChipSet,
    MatTooltip,
    MatChip,
    MatMenu,
    MatMenuTrigger,
    MatFormField,
    MatInput,
    FormsModule,
    MatRadioButton,
    MatRadioModule,
    MatChipRemove,
  ],
})
export class RoleView {
  canEdit = input(true);
  permissions = input.required<Permission[]>();
  roleId = input.required<number>();
  users = signal<User[]>([]);
  selectedUsers = signal<User[]>([]);
  private bottomSheet = inject(MatBottomSheet);
  foundUsers = signal<User[]>([]);
  choice: User | null = null;
  async loadUsers() {
    if (this.users().length) return;
    const users = await API.get<User[]>(`/moderation/roles/${this.roleId()}/users`);
    this.users.set(users);
    this.selectedUsers.set(users);
  }

  async searchUser(query: string) {
    this.choice = null;
    const foundUsers = await API.get<User[]>(`/user/search/${query}`);
    this.foundUsers.set(foundUsers);
  }

  removeUser(user: User) {
    const filtred = this.selectedUsers().filter((u) => u.accountId !== user.accountId);
    this.selectedUsers.set(filtred);
  }
  closed() {
    this.selectedUsers.set(this.users());
  }
  addUsers() {
    const choice = this.choice;
    if (!choice) return;
    const duplicated = this.selectedUsers().some((u) => u.accountId === choice.accountId);
    if (duplicated) return;
    this.selectedUsers.update((u) => [...u, choice]);
    this.choice = null;
  }
  save() {
    const originalUsers = this.users();
    const currentUsers = this.selectedUsers();

    const originalIds = new Set(originalUsers.map((u) => u.accountId));
    const currentIds = new Set(currentUsers.map((u) => u.accountId));

    const addedIds = currentUsers
      .filter((u) => !originalIds.has(u.accountId))
      .map((u) => u.accountId);

    const deletedIds = originalUsers
      .filter((u) => !currentIds.has(u.accountId))
      .map((u) => u.accountId);

    API.post(`/moderation/roles/${this.roleId()}/users`, {
      added: addedIds,
      deleted: deletedIds,
    });

    this.users.set(this.selectedUsers());
  }

  openUserSheet(user: User) {
    this.bottomSheet.open(UserSheet, { data: { user } });
  }
  changed() {
    const originalIds = new Set(this.users().map((u) => u.accountId));
    if (originalIds.size !== this.selectedUsers().length) return true;
    for (const { accountId } of this.selectedUsers()) {
      if (!originalIds.has(accountId)) return true;
    }
    return false;
  }
}
