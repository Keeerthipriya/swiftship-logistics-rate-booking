package com.swiftship.logistics.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.swiftship.logistics.api.BookingApi;
import com.swiftship.logistics.api.QuoteApi;
import com.swiftship.logistics.api.exception.BusinessExceptionMapper;
import com.swiftship.logistics.api.exception.GenericExceptionMapper;
import com.swiftship.logistics.api.exception.ValidationExceptionMapper;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Apache CXF JAX-RS Server Configuration.
 * 
 * Strict Constraint Compliance:
 * Configured via JAXRSServerFactoryBean with NO Spring MVC @RestController permitted.
 * All REST endpoints are handled exclusively through Apache CXF JAX-RS runtime.
 */
@Configuration
public class CxfJaxRsConfig {

    private final Bus bus;
    private final QuoteApi quoteApi;
    private final BookingApi bookingApi;

    public CxfJaxRsConfig(Bus bus, QuoteApi quoteApi, BookingApi bookingApi) {
        this.bus = bus;
        this.quoteApi = quoteApi;
        this.bookingApi = bookingApi;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public JacksonJsonProvider jacksonJsonProvider(ObjectMapper objectMapper) {
        return new JacksonJsonProvider(objectMapper);
    }

    @Bean
    public Server jaxRsServer(JacksonJsonProvider jacksonJsonProvider) {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(bus);
        factory.setAddress("/");

        // Register JAX-RS Resource beans (SRP: QuoteApi and BookingApi)
        factory.setServiceBeans(Arrays.asList(quoteApi, bookingApi));

        // Register JAX-RS Providers and Exception Mappers
        factory.setProviders(Arrays.asList(
                jacksonJsonProvider,
                new BusinessExceptionMapper(),
                new ValidationExceptionMapper(),
                new GenericExceptionMapper()
        ));

        return factory.create();
    }
}
