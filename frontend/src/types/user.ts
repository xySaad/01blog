export class UserMinimal {
  accountId = '';
  login = '';
}

export class User extends UserMinimal {
  firstName = '';
  lastName = '';
  followed: boolean | null = false;
  banned = false;
}

export class UserExtra extends User {
  followersCount = 0;
  followingCount = 0;
}
