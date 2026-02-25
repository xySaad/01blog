export function snake2StartCase(text: string) {
  return text
    .split('_')
    .map((s) => s.charAt(0).toUpperCase() + s.slice(1).toLocaleLowerCase())
    .join(' ');
}
