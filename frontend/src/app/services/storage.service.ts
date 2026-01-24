import { Inject, Injectable, InjectionToken } from '@angular/core';
export const DB_NAME = new InjectionToken<string>('DB_NAME');

@Injectable()
export class Storage {
  private db: Promise<IDBDatabase>;
  private name: string;
  constructor(@Inject(DB_NAME) name: string) {
    this.name = name;
    this.db = this.init();
  }

  private async init() {
    const dbs = await indexedDB.databases();
    const currentDb = dbs.find((d) => d.name === this.name);

    const req = indexedDB.open(this.name, currentDb?.version || 1);

    const { promise, resolve, reject } = Promise.withResolvers<IDBDatabase>();
    req.onupgradeneeded = reject;
    req.onsuccess = () => resolve(req.result);
    req.onblocked = req.onerror = () => reject(req.error);

    return await promise;
  }

  private async upgrade(callback: (db: IDBDatabase) => void) {
    const { promise, resolve, reject } = Promise.withResolvers<IDBDatabase>();
    const db = await this.db;
    const version = db?.version || 0;
    const req = indexedDB.open(this.name, version + 1);

    req.onupgradeneeded = () => callback(req.result);
    req.onsuccess = () => resolve(req.result);
    req.onblocked = req.onerror = () => reject(req.error);

    return await promise;
  }

  async getOrCreate(name: string, mode: IDBTransactionMode, index: string) {
    let db = await this.db;

    if (db.objectStoreNames.contains(name)) {
      const tx = db.transaction([name], mode);
      return tx.objectStore(name);
    }

    db.close();
    db = await this.upgrade((newDb) => {
      const store = newDb.createObjectStore(name);
      store.createIndex(index, index);
    });
    this.db = Promise.resolve(db);

    const tx = db.transaction([name], mode);
    return tx.objectStore(name);
  }
}
