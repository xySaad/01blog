import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import Prism from 'prismjs';
import 'prismjs/plugins/autoloader/prism-autoloader';
Prism.plugins['autoloader'].languages_path = 'assets/prism/components/';

bootstrapApplication(App, appConfig).catch((err) => console.error(err));
