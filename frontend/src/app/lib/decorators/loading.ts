import { WritableSignal } from '@angular/core';

export const WhileState = (state: (self: any) => WritableSignal<boolean>) => {
  return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
    const originalMethod: Function = descriptor.value;

    descriptor.value = async function (...args: any[]) {
      state(this).set(true);

      try {
        const returnValue = await originalMethod.call(this, ...args);
        return returnValue;
      } catch (e) {
        throw e;
      } finally {
        state(this).set(false);
      }
    };

    return descriptor;
  };
};
