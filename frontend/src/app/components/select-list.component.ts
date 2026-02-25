import { Component, computed, inject, input, model } from '@angular/core';
import { MatAnchor } from '@angular/material/button';

@Component({
  selector: 'select-list',
  template: ` <ng-content /> `,
})
export class SelectList<T> {
  value = model<T>();
}

@Component({
  selector: 'select-option',
  imports: [MatAnchor],
  host: {
    '[attr.active]': 'selected()',
  },
  template: `
    <button matButton="outlined" (click)="select()">
      <ng-content />
    </button>
  `,

  styles: [
    `
      :host[active='true'] button {
        background-color: var(
          --mat-button-tonal-container-color,
          var(--mat-sys-secondary-container)
        );
        color: var(--mat-button-tonal-label-text-color, var(--mat-sys-on-secondary-container));
        border-color: none;
      }
    `,
  ],
})
export class SelectOption<T> {
  value = input.required<T>();
  private parent = inject<SelectList<T>>(SelectList);
  selected = computed(() => this.parent.value() === this.value());

  select() {
    this.parent.value.set(this.value());
  }
}
