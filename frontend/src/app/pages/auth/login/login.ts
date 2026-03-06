import { Component, inject, signal } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatToolbar } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { API } from '../../../lib/api';
import { WhileState } from '../../../lib/decorators/loading';
import { UserService } from '../../../services/user.service';

@Component({
  styles: [
    `
      .name-highlight {
        color: var(--mat-sys-primary);
        font-weight: 700;
        letter-spacing: 0.5px;
        text-transform: capitalize;
      }
    `,
  ],
  selector: 'login',
  templateUrl: 'login.html',
  imports: [MatFormFieldModule, MatInput, MatAnchor, MatIcon, MatToolbar, MatProgressSpinner],
})
export class Login {
  router = inject(Router);
  loading = signal(false);
  readonly user = inject(UserService);

  name = '';
  data = {
    email: '',
    password: '',
  };

  email(e: any) {
    //TODO: verify format
    const value = e.target.value;
    this.data.email = value;
    this.name = value.split('@')[0];
  }
  password(e: any) {
    //TODO: verify length
    const value = e.target.value;
    this.data.password = value;
  }

  @WhileState((self) => self.loading)
  async login() {
    await API.post('/login', this.data);
    localStorage.setItem('lastLogin', Date.now().toString());
    await this.user.init();
    this.router.navigate(['/']);
  }
}
