import { Component, Inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SHIPPING_API_TOKEN } from './core/tokens/shipping-service.token';
import { ShippingApiClient } from './core/services/shipping-api.interface';
import {
  BookingConfirmation,
  BookingPayload,
  PaymentMethodType,
  PaymentSelection,
  QuoteResult,
  ShippingTierType,
  TierCardView
} from './core/models/shipping.models';
import { ParcelFormComponent } from './components/parcel-form/parcel-form.component';
import { TierSelectorComponent } from './components/tier-selector/tier-selector.component';
import { QuoteSummaryComponent } from './components/quote-summary/quote-summary.component';
import { PaymentPickerComponent } from './components/payment-picker/payment-picker.component';
import { BookingResultComponent } from './components/booking-result/booking-result.component';

/**
 * Main Application Component.
 *
 * SOLID Principles:
 * - DIP: Injects abstract ShippingApiClient through SHIPPING_API_TOKEN.
 * - SRP: Orchestrates workflow using Signals and delegates presentation to subcomponents.
 * - LSP: Reliably handles predictable domain errors (e.g. Same-Day > 100km).
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ParcelFormComponent,
    TierSelectorComponent,
    QuoteSummaryComponent,
    PaymentPickerComponent,
    BookingResultComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  // Step State
  step = signal<'BOOKING_FORM' | 'CONFIRMED'>('BOOKING_FORM');

  // Parcel & Tier Inputs
  weightKg = signal<number>(5.0);
  distanceKm = signal<number>(50.0);
  selectedTier = signal<ShippingTierType>('STANDARD');

  // Contact Info — plain properties for two-way ngModel binding
  senderName = 'Alice Johnson';
  senderAddress = '124 Market St, Suite 400, San Francisco, CA';
  recipientName = 'Bob Smith';
  recipientAddress = '789 Pine Ave, Oakland, CA';

  // Payment Inputs
  paymentMethod = signal<PaymentMethodType>('CREDIT_CARD');
  paymentReference = signal<string>('4111-2222-3333-4444');

  // Data Signals
  tiers = signal<TierCardView[]>([]);
  quote = signal<QuoteResult | null>(null);
  bookingConfirmation = signal<BookingConfirmation | null>(null);

  // Async state
  isLoadingQuote = signal<boolean>(false);
  isSubmittingBooking = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  constructor(
    @Inject(SHIPPING_API_TOKEN) private shippingApi: ShippingApiClient
  ) {}

  ngOnInit(): void {
    this.refreshTiersAndQuote();
  }

  onDimensionsChanged(dimensions: { weightKg: number; distanceKm: number }): void {
    this.weightKg.set(dimensions.weightKg);
    this.distanceKm.set(dimensions.distanceKm);
    this.refreshTiersAndQuote();
  }

  onTierSelected(tier: ShippingTierType): void {
    this.selectedTier.set(tier);
    this.refreshQuote();
  }

  onPaymentChanged(payment: PaymentSelection): void {
    this.paymentMethod.set(payment.method);
    if (payment.reference !== undefined) {
      this.paymentReference.set(payment.reference);
    }
  }

  refreshTiersAndQuote(): void {
    const w = this.weightKg();
    const d = this.distanceKm();

    this.shippingApi.getTiers(w, d).subscribe({
      next: (tierList) => {
        this.tiers.set(tierList);
        // If current selection became ineligible, fall back to first eligible tier
        const currentSelected = tierList.find(t => t.tier === this.selectedTier());
        if (currentSelected && !currentSelected.eligible) {
          const firstEligible = tierList.find(t => t.eligible);
          if (firstEligible) {
            this.selectedTier.set(firstEligible.tier);
          }
        }
        this.refreshQuote();
      },
      error: (err) => {
        this.handleError(err);
      }
    });
  }

  refreshQuote(): void {
    this.isLoadingQuote.set(true);
    this.errorMessage.set(null);

    this.shippingApi.calculateQuote({
      weightKg: this.weightKg(),
      distanceKm: this.distanceKm(),
      shippingTier: this.selectedTier()
    }).subscribe({
      next: (q) => {
        this.quote.set(q);
        this.isLoadingQuote.set(false);
      },
      error: (err) => {
        this.isLoadingQuote.set(false);
        this.quote.set(null);
        this.handleError(err);
      }
    });
  }

  submitBooking(): void {
    if (!this.senderName.trim() || !this.senderAddress.trim() ||
        !this.recipientName.trim() || !this.recipientAddress.trim()) {
      this.errorMessage.set('Please fill out all sender and recipient address fields.');
      return;
    }

    this.isSubmittingBooking.set(true);
    this.errorMessage.set(null);

    const payload: BookingPayload = {
      senderName: this.senderName.trim(),
      senderAddress: this.senderAddress.trim(),
      recipientName: this.recipientName.trim(),
      recipientAddress: this.recipientAddress.trim(),
      weightKg: this.weightKg(),
      distanceKm: this.distanceKm(),
      shippingTier: this.selectedTier(),
      paymentMethod: this.paymentMethod(),
      paymentReference: this.paymentReference()
    };

    this.shippingApi.createBooking(payload).subscribe({
      next: (confirmation) => {
        this.bookingConfirmation.set(confirmation);
        this.isSubmittingBooking.set(false);
        this.step.set('CONFIRMED');
      },
      error: (err) => {
        this.isSubmittingBooking.set(false);
        this.handleError(err);
      }
    });
  }

  resetBooking(): void {
    this.step.set('BOOKING_FORM');
    this.bookingConfirmation.set(null);
    this.errorMessage.set(null);
    this.senderName = '';
    this.senderAddress = '';
    this.recipientName = '';
    this.recipientAddress = '';
    this.refreshTiersAndQuote();
  }

  private handleError(err: any): void {
    if (err && err.error && err.error.message) {
      this.errorMessage.set(err.error.message);
    } else if (err && err.message) {
      this.errorMessage.set(err.message);
    } else {
      this.errorMessage.set('An unexpected error occurred while communicating with the logistics engine.');
    }
  }
}
