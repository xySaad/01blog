import { Component, Input, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'copy',
  templateUrl: 'copy.html',
  styles: [
    `
      .check {
        top: 0;
        left: 0;
        position: absolute;
      }
      .check,
      .copy {
        opacity: 0;
      }

      .check[active='true'],
      .copy[active='true'] {
        opacity: 1;
      }
    `,
  ],
  imports: [MatIcon],
})
export class Copy {
  active = signal(false);

  click() {
    this.active.set(true);
    setTimeout(() => {
      this.active.set(false);
    }, 3000);
  }
}
