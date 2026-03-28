import { Component, inject, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatStepperModule } from '@angular/material/stepper';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ActivatedRoute, Router } from '@angular/router';
import { API } from '../../../lib/api';
import { WhileState } from '../../../lib/decorators/loading';
import { UserService } from '../../../services/user.service';
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
  readonly user = inject(UserService);

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

  @WhileState((self) => self.loading)
  async register() {
    await API.post('/register', this.data);
    this.selectedIndex.set(1);
  }

  @WhileState((self) => self.loading)
  async verify() {
    const res = await API.post('/verify', this.data.code.toString());
    this.selectedIndex.set(2);
  }

  @WhileState((self) => self.loading)
  async createUser() {
    await API.post('/users', this.data);
    this.selectedIndex.set(3); // necessary?
    localStorage.setItem('lastLogin', Date.now().toString());
    this.router.navigate(['/']);
    this.user.init();
  }
}
