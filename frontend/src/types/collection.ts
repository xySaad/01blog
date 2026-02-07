import { Hydrator } from './api';

export const Collection = <T extends Hydrator>(Class: new () => T) => {
  return class extends Array<T> implements Hydrator {
    hydrate() {
      this.forEach((item) => {
        Object.setPrototypeOf(item, Class.prototype);
        item.hydrate();
      });
    }
  };
};
