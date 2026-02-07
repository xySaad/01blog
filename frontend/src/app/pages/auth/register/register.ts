import { Component, inject, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { global } from '../../../lib/global';
import { MatToolbarModule } from '@angular/material/toolbar';
import { API } from '../../../lib/api';
@Component({
  styles: [
    `
      .step-label {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 10px;
      }
    `,
  ],
  selector: 'register',
  templateUrl: 'register.html',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
  ],
})
export class Register {
  router = inject(Router);
  loading = signal(false);

  errors = {
    email: '',
    password: '',
  };

  data = {
    email: '',
    password: '',
    code: 0,
    login: '',
    firstName: '',
    lastName: '',
  };
  activatedRoute = inject(ActivatedRoute);
  selectedIndex = signal(0);
  constructor() {
    const step = this.activatedRoute.snapshot.queryParamMap.get('step');
    if (step === 'verify') this.selectedIndex.set(1);
    if (step === 'profile') this.selectedIndex.set(2);
  }

  email(e: any) {
    //TODO: verify format
    const value = e.target.value;
    this.data.email = value;
  }

  code(e: any) {
    //TODO: verify length
    const input = e.target;
    input.value = input.value.replace(/[^0-9]/g, '');

    const value = input.value;
    this.data.code = value;
  }
  password(e: any) {
    //TODO: verify length
    const value = e.target.value;
    this.data.password = value;
  }
  login(e: any) {
    //TODO: verify length
    const value = e.target.value;
    this.data.login = value;
  }
  firstName(e: any) {
    //TODO: verify length
    const value = e.target.value;
    this.data.firstName = value;
  }
  lastName(e: any) {
    //TODO: verify length
    const value = e.target.value;
    this.data.lastName = value;
  }

  async register() {
    this.loading.set(true);

    const body = JSON.stringify(this.data);
    try {
      await API.post('/register', body);
      this.selectedIndex.set(1);
    } catch (error) {
      //TODO: show error modal
    }

    this.loading.set(false);
  }

  async verify() {
    this.loading.set(true);
    try {
      const res = await API.post('/verify', this.data.code.toString());
      this.selectedIndex.set(2);
    } catch (error) {
      //TODO: show error modal
    }

    this.loading.set(false);
  }

  async createUser() {
    this.loading.set(true);
    try {
      const body = JSON.stringify(this.data);
      await API.post('/user', body);
      this.selectedIndex.set(3); // necessary?
      localStorage.setItem('lastLogin', Date.now().toString());
      this.router.navigate(['/']);
    } catch (error) {
      //TODO: show error modal
    }

    this.loading.set(false);
  }
}
