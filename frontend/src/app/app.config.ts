import {
  ApplicationConfig,
  ErrorHandler,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideRouter, RouteReuseStrategy } from '@angular/router';
import { CLIPBOARD_OPTIONS, provideMarkdown } from 'ngx-markdown';
import { AppReuseStrategy } from './app-reuse-strategy';
import { routes } from './app.routes';
import { Copy } from './components/copy/copy.component';
import { GlobalExceptions } from './services/global-exceptions.service';
import { UserService } from './services/user.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideMarkdown({
      clipboardOptions: { provide: CLIPBOARD_OPTIONS, useValue: { buttonComponent: Copy } },
    }),
    { provide: RouteReuseStrategy, useClass: AppReuseStrategy },
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    { provide: ErrorHandler, useClass: GlobalExceptions },
    provideAppInitializer(async () => await inject(UserService).init()),
  ],
};
