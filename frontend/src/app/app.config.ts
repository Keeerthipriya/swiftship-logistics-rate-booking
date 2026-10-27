import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { SHIPPING_API_TOKEN } from './core/tokens/shipping-service.token';
import { ShippingApiService } from './core/services/shipping-api.service';
import { MockShippingApiService } from './core/services/mock-shipping-api.service';

/**
 * Application Configuration.
 * 
 * Dependency Inversion Principle (DIP):
 * The SHIPPING_API_TOKEN is bound here to ShippingApiService for live production backend.
 * For offline testing or isolated UI development, simply swap `useClass: ShippingApiService`
 * to `useClass: MockShippingApiService`.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideHttpClient(),
    {
      provide: SHIPPING_API_TOKEN,
      useClass: ShippingApiService
    }
  ]
};
