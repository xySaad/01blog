import { Directive, DOCUMENT, inject, input, output, signal } from '@angular/core';
const STYLES = `
[loadingSpinner][loading='true'] {
  display: flex;
  align-items: center;
  gap: 5px;


  &[hideContent='true'] > * {
    display: none;
  }

  &::before {
    content: '';
    height: 0.8lh;
    aspect-ratio: 1;
    border: 0.15em solid currentColor;
    border-top-color: transparent;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}`;

@Directive({
  selector: '[loadingSpinner]',
  exportAs: 'loadingSpinner',
  host: {
    loadingSpinner: '',
    '[attr.loading]': 'loading()',
    '[attr.hideContent]': 'hideContent()',
  },
})
export class LoadingSpinner<T extends unknown> {
  loading = signal(false);
  hideContent = input(false);

  async withLoading(callback: () => T) {
    this.loading.set(true);
    try {
      const result = await callback();
      return result;
    } finally {
      this.loading.set(false);
    }
  }

  private doc = inject(DOCUMENT);
  constructor() {
    if (!this.doc.querySelector('#loading-spinner-styles')) {
      const style = this.doc.createElement('style');
      style.id = 'loading-spinner-styles';
      style.textContent = STYLES;
      this.doc.head.appendChild(style);
    }
  }
}
