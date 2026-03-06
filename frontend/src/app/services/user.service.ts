import { Injectable } from '@angular/core';
import { ApiError } from '../../types/api';
import { User } from '../../types/user';
import { API } from '../lib/api';

export class SelfUser extends User {
  permissions: string[] = [];
}

@Injectable({ providedIn: 'root' })
export class UserService {
  user = new SelfUser();
  error?: ApiError;

  async init(): Promise<void> {
    try {
      const me = await API.get<SelfUser>('/me');
      this.user = me;
      this.error = undefined;
    } catch (error) {
      if (error instanceof ApiError) {
        this.error = error;
      } else {
        throw error;
      }
    }
  }
}
