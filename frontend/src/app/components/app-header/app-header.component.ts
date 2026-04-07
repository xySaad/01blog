import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { hasPanelAccess } from '../../pages/moderation/panel/panel-page';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-header',
  templateUrl: 'app-header.html',
  styleUrls: ['app-header.css'],
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, MatTooltipModule],
})
export class AppHeader {
  private userService = inject(UserService);
  readonly user = this.userService.user;
  router = inject(Router);
  readonly showPanel = hasPanelAccess(this.user.permissions);

  isActive(path: string) {
    return this.router.url.startsWith(path);
  }
  constructor() {
    console.log(this.showPanel);
  }
  logout() {
    // this.userService.logout();
    this.router.navigateByUrl('/auth/login');
  }
}
