import { Component, input, output } from '@angular/core';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { API } from '../../../lib/api';
import { Role } from '../../../../types/role';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'role-view-header',
  styles: ':host {display: contents;}',
  template: ` <mat-panel-title> {{ role().name }} </mat-panel-title>
    <mat-panel-description>
      <div class="description">
        {{ role().description }}
      </div>
      @if (role().id !== 1 && panel()._getExpandedState() === 'expanded') {
        <div class="actions" (click)="$event.stopPropagation()">
          <button matIconButton (click)="edit.emit(role().id)">
            <mat-icon>edit</mat-icon>
          </button>
          <button matIconButton (click)="delete(role().id)">
            <mat-icon>delete</mat-icon>
          </button>
        </div>
      }
    </mat-panel-description>`,
  imports: [MatExpansionModule, MatIcon, MatButtonModule],
})
export class RoleViewHeader {
  panel = input.required<MatExpansionPanel>();
  edit = output<number>();
  role = input.required<Role>();

  delete(id: number) {
    API.delete(`/moderation/roles/${id}`);
  }
}
