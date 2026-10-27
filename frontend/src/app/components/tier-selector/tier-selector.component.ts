import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShippingTierType, TierCardView } from '../../core/models/shipping.models';

/**
 * Shipping Tier Selector Component (SRP & OCP).
 * 
 * SOLID Principles:
 * - OCP: Completely agnostic of individual tier implementations.
 *   Iterates dynamically over TierCardView array without scattering tier-specific
 *   conditionals (e.g. `*ngIf="tier === 'STANDARD'"`). Any new tier provided by
 *   the backend contract renders automatically without UI modifications.
 */
@Component({
  selector: 'app-tier-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tier-selector.component.html',
  styleUrls: ['./tier-selector.component.css']
})
export class TierSelectorComponent {
  tiers = input.required<TierCardView[]>();
  selectedTier = input<ShippingTierType>('STANDARD');

  tierSelected = output<ShippingTierType>();

  selectTier(tier: TierCardView): void {
    if (tier.eligible) {
      this.tierSelected.emit(tier.tier);
    }
  }
}
