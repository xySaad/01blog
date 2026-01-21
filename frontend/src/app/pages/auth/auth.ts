import { Component, inject, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStep, MatStepper, MatStepperModule } from '@angular/material/stepper';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { global } from '../../lib/global';
import { Router } from '@angular/router';

@Component({
  styleUrl: 'auth.css',
  templateUrl: 'auth.html',
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
  ],
})
export class Auth {
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

  email(e: any) {
    const value = e.target.value;
    this.data.email = value;
  }

  code(e: any) {
    const input = e.target;
    input.value = input.value.replace(/[^0-9]/g, '');

    const value = input.value;
    this.data.code = value;
  }
  password(e: any) {
    const value = e.target.value;
    this.data.password = value;
  }
  login(e: any) {
    const value = e.target.value;
    this.data.login = value;
  }
  firstName(e: any) {
    const value = e.target.value;
    this.data.firstName = value;
  }
  lastName(e: any) {
    const value = e.target.value;
    this.data.lastName = value;
  }

  async register(stepper: MatStepper, step: MatStep) {
    this.loading.set(true);

    const body = JSON.stringify(this.data);
    try {
      const res = await global.api.post('/register', body);
      if (res.ok) {
        step.completed = true;
        stepper.next();
      }
    } catch (error) {
      //TODO: show error modal
    }

    this.loading.set(false);
  }

  async verify(stepper: MatStepper, step: MatStep) {
    this.loading.set(true);
    try {
      const res = await global.api.post('/verify', this.data.code.toString());
      if (res.ok) {
        step.completed = true;
        stepper.next();
      }
    } catch (error) {
      //TODO: show error modal
    }

    this.loading.set(false);
  }

  async createUser() {
    this.loading.set(true);
    try {
      const body = JSON.stringify(this.data);
      const res = await global.api.post('/user', body);
      if (res.ok) {
        this.router.navigate(['/']);
      }
    } catch (error) {
      //TODO: show error modal
    }

    this.loading.set(false);
  }

  constructor() {}
}
