import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  imports: [RouterOutlet],
  styles: [
    `
      .auth {
        max-width: 600px;
        margin: auto;
      }
    `,
  ],
  template: `<div class="auth">
    <router-outlet></router-outlet>
  </div> `,
})
export class Auth {}
