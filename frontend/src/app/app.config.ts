import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, RouteReuseStrategy } from '@angular/router';
import { routes } from './app.routes';
import { CLIPBOARD_OPTIONS, provideMarkdown } from 'ngx-markdown';
import { Copy } from './components/copy/copy.component';
import { AppReuseStrategy } from './app-reuse-strategy';

export const appConfig: ApplicationConfig = {
  providers: [
    provideMarkdown({
      clipboardOptions: {
        provide: CLIPBOARD_OPTIONS,
        useValue: {
          buttonComponent: Copy,
        },
      },
    }),
    { provide: RouteReuseStrategy, useClass: AppReuseStrategy },
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
  ],
};
