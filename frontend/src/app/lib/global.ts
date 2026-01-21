export const global = {
  api: {
    endpoint: 'http://localhost:8080/api/v1',

    get: (path: string) => fetch(global.api.endpoint + path),

    post: (path: string, body: BodyInit) =>
      fetch(global.api.endpoint + path, {
        body,
        method: 'POST',
        credentials: 'include',
        headers: { 'content-type': 'application/json' },
      }),
  },
};
