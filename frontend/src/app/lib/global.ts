import 'reflect-metadata';
import { COLLECTION_TYPE } from '../../types/collection';

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

    async getJson<T>(classRef: new () => T, path: string): Promise<T> {
      const resp = fetch(global.api.endpoint + path, { credentials: 'include' });
      return typedJson(classRef, resp);
    },
    postJson: <T>(classType: new () => T, path: string, body: BodyInit) => {
      const resp = fetch(global.api.endpoint + path, {
        body,
        method: 'POST',
        credentials: 'include',
        headers: { 'content-type': 'application/json' },
      });
      return typedJson(classType, resp);
    },
  },
};

const typedJson = async <T>(classType: new () => T, resp: Promise<Response>): Promise<T> => {
  const stack: { parent: any; children: any[] }[] = [];
  const result = await resp;
  const json = JSON.parse(await result.text(), function (key, value) {
    if (key === '') return value;
    if (stack[stack.length - 1]?.parent !== this) stack.push({ parent: this, children: [] });
    stack[stack.length - 1].children = [...stack[stack.length - 1].children, { key, value }];
    return value;
  });

  let instance = new classType();
  for (const { parent, children } of stack.reverse()) {
    const collectionType = Reflect.getMetadata(COLLECTION_TYPE, classType);
    for (const { key, value } of children) {
      if (collectionType) instance = new collectionType();

      const type = Reflect.getMetadata('design:type', Object.getPrototypeOf(instance), key);
      if (type) parent[key] = new type(value);
    }
  }
  return json;
};
