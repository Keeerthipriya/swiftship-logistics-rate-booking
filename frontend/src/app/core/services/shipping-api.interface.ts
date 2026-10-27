import { Observable } from 'rxjs';
import {
  BookingConfirmation,
  BookingPayload,
  ParcelInput,
  QuoteResult,
  TierCardView
} from '../models/shipping.models';

/**
 * Service Contract for Shipping Operations (DIP & ISP).
 * Frontend components and state facades depend strictly on this abstraction.
 */
export interface ShippingApiClient {
  /**
   * Retrieves available shipping speed tiers, evaluating eligibility.
   */
  getTiers(weightKg?: number, distanceKm?: number): Observable<TierCardView[]>;

  /**
   * Calculates a rate quote for the specified parcel and speed tier.
   */
  calculateQuote(input: ParcelInput): Observable<QuoteResult>;

  /**
   * Submits a parcel booking, processes payment, and confirms booking.
   */
  createBooking(payload: BookingPayload): Observable<BookingConfirmation>;

  /**
   * Fetches an existing booking by ID.
   */
  getBooking(id: number): Observable<BookingConfirmation>;
}
