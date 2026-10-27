import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuoteResult } from '../../core/models/shipping.models';

/**
 * Presentational Quote Summary Component (SRP & ISP).
 * 
 * SOLID Principles:
 * - ISP: Only accepts the segregated QuoteResult display model,
 *   completely independent of persistence entities or payment records.
 * - SRP: Exclusively renders calculated pricing breakdown.
 */
@Component({
  selector: 'app-quote-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quote-summary.component.html',
  styleUrls: ['./quote-summary.component.css']
})
export class QuoteSummaryComponent {
  quote = input<QuoteResult | null>(null);
  loading = input<boolean>(false);
}
