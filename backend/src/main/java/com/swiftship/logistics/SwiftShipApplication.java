package com.swiftship.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SwiftShip Logistics Main Application.
 * Bootstraps Spring Boot with Apache CXF JAX-RS runtime and Spring Data JPA.
 */
@SpringBootApplication
public class SwiftShipApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftShipApplication.class, args);
    }
}
