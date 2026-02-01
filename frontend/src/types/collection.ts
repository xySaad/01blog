export const COLLECTION_TYPE = Symbol('COLLECTION_TYPE');

export class CollectionClass<T> extends Array<T> {}
export const Collection = <T>(type?: new () => T): new () => CollectionClass<T> => {
  const typedClass = class extends CollectionClass<T> {};
  Reflect.defineMetadata(COLLECTION_TYPE, type, typedClass);
  return typedClass;
};
