type User = {
  accountId: Number;
  firstName: String;
  lastName: String;
  login: String;
} | null;

export const global = {
  user: null as User,
  api: {
    endpoint: 'http://localhost:8080/api/v1',

    get: (path: string) => fetch(global.api.endpoint + path, { credentials: 'include' }),

    post: (path: string, body: BodyInit) =>
      fetch(global.api.endpoint + path, {
        body,
        method: 'POST',
        credentials: 'include',
        headers: { 'content-type': 'application/json' },
      }),
  },
};
