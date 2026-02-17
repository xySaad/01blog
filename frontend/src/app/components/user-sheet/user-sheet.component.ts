import { Component, inject } from '@angular/core';
import { MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { MatListModule } from '@angular/material/list';
import { UserHeader } from '../user-header/header.component';
import { User } from '../../../types/user';

@Component({
  selector: 'bottom-sheet-overview-example-sheet',
  templateUrl: 'user-sheet.html',
  imports: [MatListModule, UserHeader],
  styles: [
    `
      :host ::ng-deep mat-card {
        background: var(--mat-sys-surface-container-high);
      }
    `,
  ],
})
export class UserSheet {
  data: { user: User } = inject(MAT_BOTTOM_SHEET_DATA);
  constructor() {
    console.log(this.data.user);
  }
}
