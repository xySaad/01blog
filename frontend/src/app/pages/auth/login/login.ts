import { Component, inject, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatAnchor } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatToolbar } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { global } from '../../../lib/global';
import { API } from '../../../lib/api';

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

  async login() {
    this.loading.set(true);
    try {
      await API.post('/login', this.data);
      localStorage.setItem('lastLogin', Date.now().toString());
      this.router.navigate(['/']);
    } catch (error) {
      //TODO: show error modal
    }

    this.loading.set(false);
  }
}
