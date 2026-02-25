import { Component, ContentChild, input } from '@angular/core';
import { MatTab, MatTabLabel } from '@angular/material/tabs';

@Component({
  selector: 'mat-tab-keyed',
  template: `<ng-template><ng-content /></ng-template>`,
  providers: [{ provide: MatTab, useExisting: MatTabKeyed }],
})
export class MatTabKeyed<T> extends MatTab {
  tabKey = input.required<T>();

  @ContentChild(MatTabLabel) private _keyedLabel?: MatTabLabel;

  override get templateLabel(): MatTabLabel {
    return this._keyedLabel!;
  }
  override set templateLabel(x: MatTabLabel) {
    this._keyedLabel = x;
  }
}
