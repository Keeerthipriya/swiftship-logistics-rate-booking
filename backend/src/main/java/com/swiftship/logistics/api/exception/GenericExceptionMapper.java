package com.swiftship.logistics.api.exception;

import com.swiftship.logistics.api.dto.ErrorResponseDto;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top-level catch-all exception mapper for unhandled errors.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger log = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        log.error("Unhandled top-level error caught by GenericExceptionMapper", exception);
        ErrorResponseDto error = ErrorResponseDto.of(
                "SERVER_ERROR",
                "Internal server error: " + exception.getMessage(),
                500
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
