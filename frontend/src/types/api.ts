export interface Hydrator {
  hydrate(): void;
}
export type Hydratable<T extends Hydrator> = new () => T;

export class ApiError implements Error {
  name: string;
  message: string;
  status: number;

  constructor(status: number, message: string) {
    this.name = ApiError.name;
    this.status = status;
    this.message = message;
  }
}
