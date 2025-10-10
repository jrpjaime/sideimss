import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { tokenInterceptor } from './core/interceptor/token-interceptor';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { loaderInterceptor } from './core/interceptor/loader.interceptor';


export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withFetch(),
      withInterceptors([tokenInterceptor, loaderInterceptor ])  // Cambia a la función interceptor
    ),
    provideAnimationsAsync(),




  ]
};
