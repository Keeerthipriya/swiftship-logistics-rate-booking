import { InjectionToken } from '@angular/core';
import { ShippingApiClient } from '../services/shipping-api.interface';

/**
 * Angular InjectionToken for Dependency Inversion (DIP).
 * Allows components and services to inject an abstract contract,
 * making production (HTTP) and test/offline (Mock) implementations
 * completely interchangeable through Angular's DI configuration.
 */
export const SHIPPING_API_TOKEN = new InjectionToken<ShippingApiClient>('SHIPPING_API_TOKEN');
