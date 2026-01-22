import { Component } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'notfound',
  templateUrl: 'template.html',
  imports: [MatFormFieldModule],
  styles: [
    `
      h1 {
        font-family: Roboto;
        text-align: center;
      }
    `,
  ],
})
export class NotFound {}
