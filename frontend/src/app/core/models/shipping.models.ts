export type ShippingTierType = 'STANDARD' | 'EXPRESS' | 'SAME_DAY';
export type PaymentMethodType = 'CREDIT_CARD' | 'CORPORATE_ACCOUNT';
export type BookingStatusType = 'CONFIRMED' | 'CANCELLED' | 'PENDING';

/**
 * Segregated Interface (ISP): Form input representation.
 * Presentational components only manipulate this slice.
 */
export interface ParcelInput {
  weightKg: number;
  distanceKm: number;
  shippingTier: ShippingTierType;
}

/**
 * Segregated Interface (ISP): Sender and Recipient addresses.
 */
export interface ContactDetails {
  senderName: string;
  senderAddress: string;
  recipientName: string;
  recipientAddress: string;
}

/**
 * Segregated Interface (ISP): Presentational tier card view.
 * Dynamic presentation avoids hardcoded conditionals in UI templates (OCP).
 */
export interface TierCardView {
  tier: ShippingTierType;
  displayName: string;
  description: string;
  baseFee: number;
  perKgRate: number;
  perKmRate: number;
  estimatedDelivery: string;
  maxDistanceKm: number | null;
  eligible: boolean;
  ineligibilityReason: string | null;
}

/**
 * Segregated Interface (ISP): Quote calculation result.
 * Presentational quote summary only receives this display data.
 */
export interface QuoteResult {
  quoteId: string;
  shippingTier: ShippingTierType;
  weightKg: number;
  distanceKm: number;
  baseFee: number;
  weightFee: number;
  distanceFee: number;
  totalCost: number;
  currency: string;
  estimatedDelivery: string;
  createdAt: string;
}

/**
 * Segregated Interface (ISP): Payment selection data.
 */
export interface PaymentSelection {
  method: PaymentMethodType;
  reference?: string;
}

/**
 * Full booking submission payload.
 */
export interface BookingPayload extends ContactDetails, ParcelInput {
  paymentMethod: PaymentMethodType;
  paymentReference?: string;
}

/**
 * Confirmed booking confirmation model.
 */
export interface BookingConfirmation {
  bookingId: number;
  trackingNumber: string;
  status: BookingStatusType;
  senderName: string;
  senderAddress?: string;
  recipientName: string;
  recipientAddress?: string;
  shippingTier: ShippingTierType;
  weightKg: number;
  distanceKm: number;
  totalAmount: number;
  currency: string;
  paymentMethod: PaymentMethodType;
  paymentTransactionId: string;
  estimatedDelivery: string;
  createdAt: string;
}
