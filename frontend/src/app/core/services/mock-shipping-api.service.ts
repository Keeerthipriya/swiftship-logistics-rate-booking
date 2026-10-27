import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ShippingApiClient } from './shipping-api.interface';
import {
  BookingConfirmation,
  BookingPayload,
  ParcelInput,
  QuoteResult,
  TierCardView
} from '../models/shipping.models';

/**
 * Mock implementation of ShippingApiClient.
 * 
 * SOLID Principles:
 * - DIP: Fulfills ShippingApiClient interface, swappable via Angular DI token.
 * - LSP: Accurately adheres to all preconditions and outcomes specified in the contract.
 *   Throws predictable domain error when Same-Day is requested for > 100km.
 */
@Injectable({
  providedIn: 'root'
})
export class MockShippingApiService implements ShippingApiClient {

  private readonly tiersMaster: TierCardView[] = [
    {
      tier: 'STANDARD',
      displayName: 'Standard Delivery',
      description: 'Cost-effective shipping arriving in 3–5 business days.',
      baseFee: 5.0,
      perKgRate: 1.2,
      perKmRate: 0.05,
      estimatedDelivery: '3–5 business days',
      maxDistanceKm: null,
      eligible: true,
      ineligibilityReason: null
    },
    {
      tier: 'EXPRESS',
      displayName: 'Express Delivery',
      description: 'Fast guaranteed delivery next business day.',
      baseFee: 15.0,
      perKgRate: 2.5,
      perKmRate: 0.12,
      estimatedDelivery: 'Next day',
      maxDistanceKm: null,
      eligible: true,
      ineligibilityReason: null
    },
    {
      tier: 'SAME_DAY',
      displayName: 'Same-Day Delivery',
      description: 'Direct courier rush delivery within 4 hours (restricted to <= 100 km).',
      baseFee: 35.0,
      perKgRate: 4.0,
      perKmRate: 0.25,
      estimatedDelivery: 'Within 4 hours',
      maxDistanceKm: 100.0,
      eligible: true,
      ineligibilityReason: null
    }
  ];

  getTiers(weightKg?: number, distanceKm?: number): Observable<TierCardView[]> {
    const list = this.tiersMaster.map(t => {
      let eligible = true;
      let ineligibilityReason = null;
      if (distanceKm !== undefined && distanceKm !== null && t.maxDistanceKm !== null && distanceKm > t.maxDistanceKm) {
        eligible = false;
        ineligibilityReason = `${t.displayName} is restricted to distances <= ${t.maxDistanceKm} km`;
      }
      return { ...t, eligible, ineligibilityReason };
    });
    return of(list).pipe(delay(100));
  }

  calculateQuote(input: ParcelInput): Observable<QuoteResult> {
    if (input.weightKg < 0.1 || input.weightKg > 50.0) {
      return throwError(() => ({
        errorCode: 'INVALID_PARCEL_PARAMETERS',
        message: 'Weight must be between 0.1 kg and 50.0 kg',
        status: 400
      }));
    }
    if (input.distanceKm < 1.0 || input.distanceKm > 2500.0) {
      return throwError(() => ({
        errorCode: 'INVALID_PARCEL_PARAMETERS',
        message: 'Distance must be between 1 km and 2,500 km',
        status: 400
      }));
    }

    if (input.shippingTier === 'SAME_DAY' && input.distanceKm > 100.0) {
      return throwError(() => ({
        errorCode: 'TIER_NOT_ELIGIBLE',
        message: `Same-Day delivery is restricted to distances <= 100 km (requested: ${input.distanceKm} km)`,
        status: 422
      }));
    }

    const tierInfo = this.tiersMaster.find(t => t.tier === input.shippingTier) || this.tiersMaster[0];
    const baseFee = tierInfo.baseFee;
    const weightFee = Number((input.weightKg * tierInfo.perKgRate).toFixed(2));
    const distanceFee = Number((input.distanceKm * tierInfo.perKmRate).toFixed(2));
    const totalCost = Number((baseFee + weightFee + distanceFee).toFixed(2));

    const result: QuoteResult = {
      quoteId: 'mock-' + Math.random().toString(36).substring(2, 9),
      shippingTier: input.shippingTier,
      weightKg: input.weightKg,
      distanceKm: input.distanceKm,
      baseFee,
      weightFee,
      distanceFee,
      totalCost,
      currency: 'USD',
      estimatedDelivery: tierInfo.estimatedDelivery,
      createdAt: new Date().toISOString()
    };

    return of(result).pipe(delay(150));
  }

  createBooking(payload: BookingPayload): Observable<BookingConfirmation> {
    // Validate SameDay distance
    if (payload.shippingTier === 'SAME_DAY' && payload.distanceKm > 100.0) {
      return throwError(() => ({
        errorCode: 'TIER_NOT_ELIGIBLE',
        message: `Same-Day delivery is restricted to distances <= 100 km (requested: ${payload.distanceKm} km)`,
        status: 422
      }));
    }

    const tierInfo = this.tiersMaster.find(t => t.tier === payload.shippingTier) || this.tiersMaster[0];
    const totalAmount = Number((tierInfo.baseFee + (payload.weightKg * tierInfo.perKgRate) + (payload.distanceKm * tierInfo.perKmRate)).toFixed(2));
    const year = new Date().getFullYear();
    const trackingNumber = `SWIFT-${year}-${Math.random().toString(36).substring(2, 8).toUpperCase()}`;

    const confirmation: BookingConfirmation = {
      bookingId: Math.floor(1000 + Math.random() * 9000),
      trackingNumber,
      status: 'CONFIRMED',
      senderName: payload.senderName,
      senderAddress: payload.senderAddress,
      recipientName: payload.recipientName,
      recipientAddress: payload.recipientAddress,
      shippingTier: payload.shippingTier,
      weightKg: payload.weightKg,
      distanceKm: payload.distanceKm,
      totalAmount,
      currency: 'USD',
      paymentMethod: payload.paymentMethod,
      paymentTransactionId: `TXN-SIM-${Math.random().toString(36).substring(2, 8).toUpperCase()}`,
      estimatedDelivery: tierInfo.estimatedDelivery,
      createdAt: new Date().toISOString()
    };

    return of(confirmation).pipe(delay(250));
  }

  getBooking(id: number): Observable<BookingConfirmation> {
    const confirmation: BookingConfirmation = {
      bookingId: id,
      trackingNumber: `SWIFT-2026-ARCHIVE`,
      status: 'CONFIRMED',
      senderName: 'Swift Customer',
      recipientName: 'Swift Recipient',
      shippingTier: 'STANDARD',
      weightKg: 5.0,
      distanceKm: 120.0,
      totalAmount: 17.0,
      currency: 'USD',
      paymentMethod: 'CREDIT_CARD',
      paymentTransactionId: 'TXN-MOCK-ARCHIVE',
      estimatedDelivery: '3–5 business days',
      createdAt: new Date().toISOString()
    };
    return of(confirmation).pipe(delay(100));
  }
}
