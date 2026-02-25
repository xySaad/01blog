import { ApiError, Hydratable, Hydrator } from '../../types/api';

async function fetchJson<T>(method: string, path: string, init?: RequestInit): Promise<T>;

async function fetchJson<T extends Hydrator>(
  method: string,
  path: string,
  init: RequestInit | undefined,
  Class: Hydratable<T>,
): Promise<T>;

async function fetchJson<T>(
  method: string,
  path: string,
  init?: RequestInit,
  Class?: Hydratable<any>,
): Promise<T> {
  const resp = await fetch(API.ENDPOINT + path, { method, credentials: 'include', ...init });

  if (!resp.ok) {
    const text = await resp.text();
    throw new ApiError(resp.status, text);
  }

  const contentType = resp.headers.get('content-type');
  if (!contentType?.includes('application/json')) {
    const text = await resp.text();
    return (text || {}) as T;
  }

  const json = await resp.json();

  if (Class) {
    Object.setPrototypeOf(json, Class.prototype);
    json.hydrate();
  }

  return json;
}
export const API = {
  ENDPOINT: 'http://localhost:8080/api/v1',

  get<T>(path: string) {
    return fetchJson<T>('GET', path);
  },

  getH<T extends Hydrator>(Class: Hydratable<T>, path: string) {
    return fetchJson('GET', path, undefined, Class);
  },

  delete<T>(path: string) {
    return fetchJson<T>('DELETE', path);
  },

  deleteH<T extends Hydrator>(Class: Hydratable<T>, path: string) {
    return fetchJson('DELETE', path, undefined, Class);
  },

  put<T>(path: string, body?: any) {
    console.log(body);

    const init = {
      body: JSON.stringify(body),
      headers: { 'Content-Type': 'application/json' },
    };

    return fetchJson<T>('PUT', path, init);
  },

  putH<T extends Hydrator>(Class: Hydratable<T>, path: string, body?: any) {
    const init = {
      body: JSON.stringify(body),
      headers: { 'Content-Type': 'application/json' },
    };

    return fetchJson('PUT', path, init, Class);
  },

  post<T>(path: string, body: any, headers?: HeadersInit) {
    const init = {
      body: JSON.stringify(body),
      headers: { 'Content-Type': 'application/json', ...headers },
    };

    return fetchJson<T>('POST', path, init);
  },

  postRaw<T>(path: string, body: any, headers?: HeadersInit) {
    return fetchJson<T>('POST', path, { body, headers });
  },
  postH<T extends Hydrator>(Class: Hydratable<T>, path: string, body: any, headers?: HeadersInit) {
    const init = {
      body: JSON.stringify(body),
      headers: { 'Content-Type': 'application/json', ...headers },
    };

    return fetchJson('POST', path, init, Class);
  },
};
