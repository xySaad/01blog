export const enumValues = <T extends { [k: string]: any }>(Enum: T) =>
  Object.values(Enum).filter((v) => typeof v === 'string') as (keyof T)[];

export const enumString = <T extends string>(...keys: T[]) => {
  const o: { [P in T]: P } = {} as any;
  for (const key of keys) {
    o[key] = key;
  }
  return o;
};
