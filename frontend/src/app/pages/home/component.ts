import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';

@Component({
  templateUrl: 'template.html',
  imports: [MatButtonModule, MatIcon],
})
export class Home {
  router = inject(Router);
}
