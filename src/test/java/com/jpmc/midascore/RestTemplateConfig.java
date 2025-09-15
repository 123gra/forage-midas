package com.jpmc.midascore;

// 📦 Importing Spring configuration and HTTP client components
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * ⚙️ RestTemplateConfig provides a centralized configuration for RestTemplate.
 * This enables consistent HTTP client behavior across the application.
 */
@Configuration
public class RestTemplateConfig
{
    /**
     * 🛠️ Bean definition for RestTemplate.
     * Uses RestTemplateBuilder for flexible customization (timeouts, interceptors, etc.).
     *
     * @param builder Spring Boot's RestTemplateBuilder
     * @return a configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }
}