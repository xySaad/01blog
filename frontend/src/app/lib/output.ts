import { OutputEmitterRef, OutputOptions } from '@angular/core';

export class BetterOutputEmitterRef<T> extends OutputEmitterRef<T> {}

declare function betterOutput<T = void>(opts?: OutputOptions): BetterOutputEmitterRef<T>;
