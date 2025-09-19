package com.jpmc.midascore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * Defines a RestTemplate bean for synchronous REST client calls.
     * This bean is required by the TransactionListener.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}