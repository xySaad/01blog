import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  templateUrl: 'template.html',
  imports: [MatIcon, MatButtonModule],
})
export class Home {}
