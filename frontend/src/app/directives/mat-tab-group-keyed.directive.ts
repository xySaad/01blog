import { Directive, contentChildren, effect, inject, input } from '@angular/core';
import { MatTabGroup } from '@angular/material/tabs';
import { MatTabKeyed } from '../components/mat-tab-keyed.component';

@Directive({
  selector: 'mat-tab-group[selectedKey]',
})
export class MatTabGroupKeyed<T> {
  selectedKey = input.required<T>();

  private tabGroup = inject(MatTabGroup);
  private tabs = contentChildren(MatTabKeyed<T>);

  constructor() {
    effect(() => {
      const index = this.tabs().findIndex((t) => t.tabKey() === this.selectedKey());
      if (index >= 0) this.tabGroup.selectedIndex = index;
    });
  }
}
