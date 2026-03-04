import { WritableSignal } from '@angular/core';

export const WhileState = <T extends unknown>(state: (self: T) => WritableSignal<boolean>) => {
  return function (target: T, propertyKey: string, descriptor: PropertyDescriptor) {
    const originalMethod: Function = descriptor.value;

    descriptor.value = async function (...args: any[]) {
      state(this as T).set(true);

      try {
        const returnValue = await originalMethod.call(this, ...args);
        return returnValue;
      } catch (e) {
        throw e;
      } finally {
        state(this as T).set(false);
      }
    };

    return descriptor;
  };
};
