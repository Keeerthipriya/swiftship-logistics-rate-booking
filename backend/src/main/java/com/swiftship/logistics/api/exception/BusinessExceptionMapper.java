package com.swiftship.logistics.api.exception;

import com.swiftship.logistics.api.dto.ErrorResponseDto;
import com.swiftship.logistics.domain.exception.InvalidParcelException;
import com.swiftship.logistics.domain.exception.PaymentFailedException;
import com.swiftship.logistics.domain.exception.ResourceNotFoundException;
import com.swiftship.logistics.domain.exception.TierNotEligibleException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * JAX-RS ExceptionMapper that transforms business and domain exceptions
 * into structured, predictable HTTP error responses.
 * 
 * Liskov Substitution Principle (LSP):
 * Translates domain rejection events (e.g. Same-Day > 100km) into clear 422/400
 * HTTP responses without letting unhandled runtime failures leak.
 */
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger log = LoggerFactory.getLogger(BusinessExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException exception) {
        log.warn("Handling domain exception: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        if (exception instanceof TierNotEligibleException tierEx) {
            ErrorResponseDto error = ErrorResponseDto.of(
                    "TIER_NOT_ELIGIBLE",
                    tierEx.getMessage(),
                    422,
                    List.of(String.format("Tier %s is restricted to distances <= %.0f km",
                            tierEx.getTier(), tierEx.getMaxAllowedKm()))
            );
            return Response.status(422)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        if (exception instanceof InvalidParcelException invalidEx) {
            ErrorResponseDto error = ErrorResponseDto.of(
                    "INVALID_PARCEL_PARAMETERS",
                    invalidEx.getMessage(),
                    400
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        if (exception instanceof PaymentFailedException paymentEx) {
            ErrorResponseDto error = ErrorResponseDto.of(
                    "PAYMENT_REJECTED",
                    paymentEx.getMessage(),
                    422
            );
            return Response.status(422)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        if (exception instanceof ResourceNotFoundException notFoundEx) {
            ErrorResponseDto error = ErrorResponseDto.of(
                    "RESOURCE_NOT_FOUND",
                    notFoundEx.getMessage(),
                    404
            );
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        if (exception instanceof IllegalArgumentException illegalEx) {
            ErrorResponseDto error = ErrorResponseDto.of(
                    "ILLEGAL_ARGUMENT",
                    illegalEx.getMessage(),
                    400
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        // Default fallback for other runtime exceptions
        log.error("Unhandled runtime exception", exception);
        ErrorResponseDto error = ErrorResponseDto.of(
                "INTERNAL_ERROR",
                "An unexpected server error occurred: " + exception.getMessage(),
                500
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
