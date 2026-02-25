import { signal } from '@angular/core';

export function requiredSelect<T>(defaultValue: T) {
  let internal = defaultValue;

  return {
    get value(): T {
      return internal;
    },
    set value(x: T | undefined | null) {
      internal = x ?? defaultValue;
    },
  };
}
