package com.fashionplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the FashionPlace marketplace application.
 */
@SpringBootApplication
public class FashionPlaceApplication {

    /**
     * Starts the embedded web server and Spring application context.
     *
     * @param args optional command-line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(FashionPlaceApplication.class, args);
    }
}
