import { Component, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

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
  loading = signal(false);
  errors = {
    email: '',
    password: '',
  };

  email(e: any) {
    const value = e.target.value;
  }

  password(e: any) {
    const value = e.target.value;
  }

  register() {
    this.loading.set(true);
    setTimeout(() => {
      this.loading.set(false);
    }, 3000);
  }

  constructor() {}
}
