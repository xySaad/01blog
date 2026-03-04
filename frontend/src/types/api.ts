export interface Hydrator {
  hydrate(): void;
}
export type Hydratable<T extends Hydrator> = new () => T;

export class FetchError extends Error {
  constructor(message: string) {
    super(message);
    this.name = this.constructor.name;
  }
}

export class ApiError extends FetchError {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}
