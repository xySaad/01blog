import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  host: {
    class: 'mat-typography',
  },
})
export class App {
  protected readonly title = signal('01blog');
}
