package com.swiftship.logistics.service;

import com.swiftship.logistics.api.dto.BookingRequestDto;
import com.swiftship.logistics.api.dto.BookingResponseDto;
import com.swiftship.logistics.api.dto.QuoteRequestDto;
import com.swiftship.logistics.api.dto.QuoteResponseDto;
import com.swiftship.logistics.domain.ParcelBooking;
import com.swiftship.logistics.domain.PaymentMethod;
import com.swiftship.logistics.domain.ShippingTier;
import com.swiftship.logistics.domain.exception.PaymentFailedException;
import com.swiftship.logistics.payment.PaymentCoordinator;
import com.swiftship.logistics.payment.PaymentResult;
import com.swiftship.logistics.repository.BookingRepository;
import com.swiftship.logistics.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Demonstrates Dependency Inversion Principle (DIP):
 * BookingServiceImpl depends strictly on abstractions (QuoteService, PaymentCoordinator, BookingRepository).
 * This test replaces the payment infrastructure with a mocked/alternate implementation
 * via constructor injection, proving full decoupling from concrete infrastructure.
 */
@ExtendWith(MockitoExtension.class)
class ReplaceableDependencyTest {

    @Mock
    private QuoteService mockQuoteService;

    @Mock
    private PaymentCoordinator mockPaymentCoordinator;

    @Mock
    private BookingRepository mockBookingRepository;

    @Test
    @DisplayName("DIP Test: BookingServiceImpl seamlessly accepts substituted mock dependencies")
    void testSubstitutedPaymentDependencySuccess() {
        // Constructor injection of abstractions
        BookingService serviceWithReplacedDependencies = new BookingServiceImpl(
                mockQuoteService,
                mockPaymentCoordinator,
                mockBookingRepository
        );

        QuoteResponseDto stubbedQuote = new QuoteResponseDto(
                UUID.randomUUID(),
                ShippingTier.STANDARD,
                5.0,
                50.0,
                new BigDecimal("5.00"),
                new BigDecimal("6.00"),
                new BigDecimal("2.50"),
                new BigDecimal("13.50"),
                "USD",
                "3\u20135 business days",
                Instant.now()
        );

        when(mockQuoteService.calculateQuote(any(QuoteRequestDto.class))).thenReturn(stubbedQuote);
        when(mockPaymentCoordinator.executePayment(eq(PaymentMethod.CREDIT_CARD), any(), eq("USD"), any()))
                .thenReturn(PaymentResult.success("TXN-MOCK-99999", "AUTH-MOCK-123"));

        when(mockBookingRepository.save(any(ParcelBooking.class))).thenAnswer(invocation -> {
            ParcelBooking entity = invocation.getArgument(0);
            entity.setId(42L);
            return entity;
        });

        BookingRequestDto request = new BookingRequestDto(
                "Jane Doe",
                "123 Maple St",
                "John Doe",
                "456 Oak St",
                5.0,
                50.0,
                ShippingTier.STANDARD,
                PaymentMethod.CREDIT_CARD,
                "4111-TEST"
        );

        BookingResponseDto response = serviceWithReplacedDependencies.createBooking(request);

        assertThat(response.bookingId()).isEqualTo(42L);
        assertThat(response.paymentTransactionId()).isEqualTo("TXN-MOCK-99999");
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("13.50"));

        verify(mockQuoteService, times(1)).calculateQuote(any());
        verify(mockPaymentCoordinator, times(1)).executePayment(eq(PaymentMethod.CREDIT_CARD), any(), eq("USD"), any());
        verify(mockBookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("DIP Test: Swapped payment dependency triggers expected failure behavior")
    void testSubstitutedPaymentDependencyFailure() {
        BookingService serviceWithReplacedDependencies = new BookingServiceImpl(
                mockQuoteService,
                mockPaymentCoordinator,
                mockBookingRepository
        );

        QuoteResponseDto stubbedQuote = new QuoteResponseDto(
                UUID.randomUUID(),
                ShippingTier.STANDARD,
                5.0,
                50.0,
                new BigDecimal("5.00"),
                new BigDecimal("6.00"),
                new BigDecimal("2.50"),
                new BigDecimal("13.50"),
                "USD",
                "3\u20135 business days",
                Instant.now()
        );

        when(mockQuoteService.calculateQuote(any(QuoteRequestDto.class))).thenReturn(stubbedQuote);
        when(mockPaymentCoordinator.executePayment(any(), any(), any(), any()))
                .thenThrow(new PaymentFailedException("Card expired or declined"));

        BookingRequestDto request = new BookingRequestDto(
                "Jane Doe",
                "123 Maple St",
                "John Doe",
                "456 Oak St",
                5.0,
                50.0,
                ShippingTier.STANDARD,
                PaymentMethod.CREDIT_CARD,
                "4111-TEST"
        );

        assertThatThrownBy(() -> serviceWithReplacedDependencies.createBooking(request))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Card expired or declined");

        // Verify repository is never invoked when payment fails (Transactional integrity)
        verify(mockBookingRepository, never()).save(any());
    }
}
