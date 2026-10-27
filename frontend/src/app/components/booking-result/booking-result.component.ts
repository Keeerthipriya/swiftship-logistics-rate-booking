import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingConfirmation } from '../../core/models/shipping.models';

/**
 * Booking Result Component (SRP & ISP).
 * Displays receipt and tracking information for confirmed parcel booking.
 */
@Component({
  selector: 'app-booking-result',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './booking-result.component.html',
  styleUrls: ['./booking-result.component.css']
})
export class BookingResultComponent {
  booking = input.required<BookingConfirmation>();
  resetRequested = output<void>();

  onReset(): void {
    this.resetRequested.emit();
  }
}
