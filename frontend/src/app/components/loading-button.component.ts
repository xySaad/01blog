import { Directive, input } from '@angular/core';
import { LoadingSpinner } from '../directives/loading-spinner.directive';

@Directive({
  selector: 'button[loadingButton]',
  host: {
    '(click)': 'this.withLoading(onClick())',
    '[disabled]': 'disabled() || loading()',
  },
})
export class LoadingButton<T extends unknown> extends LoadingSpinner<T> {
  'onClick' = input.required<() => T>();
  disabled = input(false);
}
