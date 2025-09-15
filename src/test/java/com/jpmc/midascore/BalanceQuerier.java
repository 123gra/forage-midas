package com.jpmc.midascore;

// 📦 Importing required Spring and HTTP client components
import com.jpmc.midascore.foundation.Balance;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 🌐 BalanceQuerier is a client-side utility for querying user balances
 * from an external service via REST API.
 */
@Component
public class BalanceQuerier
{
    // 🔧 RestTemplate used to perform HTTP GET requests
    private final RestTemplate restTemplate;

    /**
     * 🛠️ Constructor-based injection using RestTemplateBuilder.
     * Allows customization of RestTemplate if needed (timeouts, interceptors, etc.).
     *
     * @param builder Spring Boot's RestTemplateBuilder
     */
    public BalanceQuerier(RestTemplateBuilder builder)
    {
        this.restTemplate = builder.build();
    }

    /**
     * 📡 Queries the balance of a user by making a GET request to the balance endpoint.
     *
     * @param userId the ID of the user whose balance is being requested
     * @return Balance object containing the user's balance
     */
    public Balance query(Long userId)
    {
        String url = "http://localhost:33400/balance?userId=" + userId;
        return restTemplate.getForObject(url, Balance.class);
    }
}