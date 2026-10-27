import { Component, input, output, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/**
 * Parcel Form Component (SRP).
 * Responsible exclusively for capturing and validating parcel weight and distance.
 * Uses Angular Signals for state reactivity. No direct DOM manipulation.
 */
@Component({
  selector: 'app-parcel-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './parcel-form.component.html',
  styleUrls: ['./parcel-form.component.css']
})
export class ParcelFormComponent {
  // Input signals from parent
  initialWeight = input<number>(5.0);
  initialDistance = input<number>(50.0);

  // Form field state signals
  weight = signal<number>(5.0);
  distance = signal<number>(50.0);

  // Validation state signals
  weightError = signal<string | null>(null);
  distanceError = signal<string | null>(null);

  // Output event signals
  dimensionsChanged = output<{ weightKg: number; distanceKm: number }>();

  constructor() {
    effect(() => {
      this.weight.set(this.initialWeight());
      this.distance.set(this.initialDistance());
    });
  }

  onWeightChange(value: number): void {
    this.weight.set(value);
    this.validateAndEmit();
  }

  onDistanceChange(value: number): void {
    this.distance.set(value);
    this.validateAndEmit();
  }

  private validateAndEmit(): void {
    const w = this.weight();
    const d = this.distance();

    let valid = true;
    if (w === null || w === undefined || isNaN(w) || w < 0.1 || w > 50.0) {
      this.weightError.set('Weight must be between 0.1 kg and 50.0 kg');
      valid = false;
    } else {
      this.weightError.set(null);
    }

    if (d === null || d === undefined || isNaN(d) || d < 1.0 || d > 2500.0) {
      this.distanceError.set('Distance must be between 1 km and 2,500 km');
      valid = false;
    } else {
      this.distanceError.set(null);
    }

    if (valid) {
      this.dimensionsChanged.emit({ weightKg: w, distanceKm: d });
    }
  }
}
