package com.swiftship.logistics.service;

import com.swiftship.logistics.api.dto.BookingRequestDto;
import com.swiftship.logistics.api.dto.BookingResponseDto;
import com.swiftship.logistics.domain.BookingStatus;
import com.swiftship.logistics.domain.PaymentMethod;
import com.swiftship.logistics.domain.ShippingTier;
import com.swiftship.logistics.domain.exception.InvalidParcelException;
import com.swiftship.logistics.domain.exception.TierNotEligibleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Test
    @DisplayName("Successfully create booking and persist in H2 database")
    void testCreateBookingSuccess() {
        BookingRequestDto request = new BookingRequestDto(
                "Keerthi Priya",
                "100 Tech Park, Suite 200, Seattle, WA",
                "Swift Logistics Hub",
                "500 Cargo Way, Portland, OR",
                5.0,
                250.0,
                ShippingTier.STANDARD,
                PaymentMethod.CREDIT_CARD,
                "4111222233334444"
        );

        BookingResponseDto response = bookingService.createBooking(request);

        assertThat(response).isNotNull();
        assertThat(response.bookingId()).isNotNull();
        assertThat(response.trackingNumber()).startsWith("SWIFT-");
        assertThat(response.status()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(response.senderName()).isEqualTo("Keerthi Priya");
        assertThat(response.senderAddress()).isEqualTo("100 Tech Park, Suite 200, Seattle, WA");
        assertThat(response.recipientName()).isEqualTo("Swift Logistics Hub");
        assertThat(response.shippingTier()).isEqualTo(ShippingTier.STANDARD);
        // Base: $5.00 + 5.0 * 1.20 ($6.00) + 250.0 * 0.05 ($12.50) = $23.50
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("23.50"));
        assertThat(response.paymentTransactionId()).startsWith("TXN-CC-");

        // Verify retrieval by ID from H2
        BookingResponseDto fetched = bookingService.getBookingById(response.bookingId());
        assertThat(fetched.trackingNumber()).isEqualTo(response.trackingNumber());
        assertThat(fetched.totalAmount()).isEqualByComparingTo(new BigDecimal("23.50"));
    }

    @Test
    @DisplayName("LSP Rejection in Booking: Same-Day with distance > 100km throws TierNotEligibleException")
    void testBookingSameDayOver100KmRejection() {
        BookingRequestDto request = new BookingRequestDto(
                "Keerthi Priya",
                "100 Tech Park, Suite 200, Seattle, WA",
                "Swift Logistics Hub",
                "500 Cargo Way, Portland, OR",
                5.0,
                150.0, // > 100 km!
                ShippingTier.SAME_DAY,
                PaymentMethod.CORPORATE_ACCOUNT,
                "CORP-9921"
        );

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(TierNotEligibleException.class)
                .hasMessageContaining("restricted to distances <= 100 km");
    }

    @Test
    @DisplayName("Validation Rejection in Booking: Weight exceeding 50kg throws InvalidParcelException")
    void testBookingExcessiveWeightRejection() {
        BookingRequestDto request = new BookingRequestDto(
                "Keerthi Priya",
                "100 Tech Park, Suite 200, Seattle, WA",
                "Swift Logistics Hub",
                "500 Cargo Way, Portland, OR",
                65.0, // > 50 kg!
                50.0,
                ShippingTier.EXPRESS,
                PaymentMethod.CREDIT_CARD,
                "4111222233334444"
        );

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(InvalidParcelException.class)
                .hasMessageContaining("Weight must be between 0.1 kg and 50.0 kg");
    }
}
