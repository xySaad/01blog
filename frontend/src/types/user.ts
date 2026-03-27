import { Hydrator } from './api';

export class User {
  accountId = '';
  firstName = '';
  lastName = '';
  login = '';
  followed: boolean | null = false;
  banned = false;
}

export class UserExtra extends User {
  followersCount = 0;
  followingCount = 0;
}
