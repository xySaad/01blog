import { CreateSignalOptions, signal, WritableSignal } from '@angular/core';

export const betterSignal = <T extends {}>(
  initialValue: T,
  options?: CreateSignalOptions<T>,
): BetterSignal<T> => {
  let changed = false;

  const isEqual = (a: T, b: T) => {
    if (changed) return (changed = false);
    if (options?.equal) return options.equal(a, b);
    return a == b;
  };

  const sig = signal(initialValue, { ...options, equal: isEqual }) as BetterSignal<T>;
  sig.patch = (patchFn: (prev: T) => T) => {
    changed = true;
    sig.update(patchFn);
  };
  return sig;
};

export interface BetterSignal<T> extends WritableSignal<T> {
  patch(patchFn: (prev: T) => T): void;
}
