import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ShippingApiClient } from './shipping-api.interface';
import {
  BookingConfirmation,
  BookingPayload,
  ParcelInput,
  QuoteResult,
  TierCardView
} from '../models/shipping.models';

/**
 * Production implementation of ShippingApiClient.
 * Communicates with the backend CXF JAX-RS service via the Node proxy at /api.
 */
@Injectable({
  providedIn: 'root'
})
export class ShippingApiService implements ShippingApiClient {
  private readonly baseUrl = '/api';

  constructor(private http: HttpClient) {}

  getTiers(weightKg?: number, distanceKm?: number): Observable<TierCardView[]> {
    let params = new HttpParams();
    if (weightKg !== undefined && weightKg !== null) {
      params = params.set('weightKg', weightKg.toString());
    }
    if (distanceKm !== undefined && distanceKm !== null) {
      params = params.set('distanceKm', distanceKm.toString());
    }
    return this.http.get<TierCardView[]>(`${this.baseUrl}/tiers`, { params });
  }

  calculateQuote(input: ParcelInput): Observable<QuoteResult> {
    return this.http.post<QuoteResult>(`${this.baseUrl}/quotes`, input);
  }

  createBooking(payload: BookingPayload): Observable<BookingConfirmation> {
    return this.http.post<BookingConfirmation>(`${this.baseUrl}/bookings`, payload);
  }

  getBooking(id: number): Observable<BookingConfirmation> {
    return this.http.get<BookingConfirmation>(`${this.baseUrl}/bookings/${id}`);
  }
}
