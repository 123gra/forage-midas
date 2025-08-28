package com.jpmc.midascore;

// 🚀 Spring Boot core imports for application startup and configuration
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 🏁 Entry point for the MidasCore Spring Boot application.
 * This class bootstraps the entire application context.
 */
@SpringBootApplication
public class MidasCoreApplication
{
    /**
     * 🔧 Main method to launch the Spring Boot application.
     * SpringApplication.run initializes the context and starts embedded server.
     *
     * @param args command-line arguments passed during startup
     */
    public static void main(String[] args)
    {
        SpringApplication.run(MidasCoreApplication.class, args);
    }
}