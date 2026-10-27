import { Component, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentMethodType, PaymentSelection } from '../../core/models/shipping.models';

/**
 * Payment Picker Component (SRP).
 * Responsible for selecting payment mode and simulated credential input.
 */
@Component({
  selector: 'app-payment-picker',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-picker.component.html',
  styleUrls: ['./payment-picker.component.css']
})
export class PaymentPickerComponent {
  selectedMethod = signal<PaymentMethodType>('CREDIT_CARD');
  reference = signal<string>('4111-2222-3333-4444');

  paymentChanged = output<PaymentSelection>();

  onMethodSelect(method: PaymentMethodType): void {
    this.selectedMethod.set(method);
    if (method === 'CREDIT_CARD') {
      this.reference.set('4111-2222-3333-4444');
    } else {
      this.reference.set('CORP-ACCT-98421');
    }
    this.emitChange();
  }

  onReferenceChange(val: string): void {
    this.reference.set(val);
    this.emitChange();
  }

  private emitChange(): void {
    this.paymentChanged.emit({
      method: this.selectedMethod(),
      reference: this.reference()
    });
  }
}
