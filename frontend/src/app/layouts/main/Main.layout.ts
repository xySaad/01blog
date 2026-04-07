import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppHeader } from '../../components/app-header/app-header.component';

@Component({
  templateUrl: `Main.layout.html`,
  imports: [RouterOutlet, AppHeader],
})
export class MainLayout {}
