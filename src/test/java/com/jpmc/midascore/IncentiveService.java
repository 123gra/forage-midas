package com.jpmc.midascore;

// 📦 Importing required logging, service, and HTTP client components
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 🎁 IncentiveService handles communication with the external incentive engine.
 * It sends transaction data and retrieves incentive details via REST API.
 */
@Service
public class IncentiveService
{
    // 📝 Logger for tracking service activity and debugging
    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);

    // 🌐 RestTemplate used for making HTTP POST requests
    private final RestTemplate restTemplate;

    /**
     * 🛠️ Constructor-based injection of RestTemplate.
     * Allows for customization and testing flexibility.
     *
     * @param restTemplate the HTTP client used to communicate with external services
     */
    public IncentiveService(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    /**
     * 📡 Sends a transaction to the incentive engine and retrieves the computed incentive.
     *
     * @param transaction the transaction details to be evaluated
     * @return Incentive object containing the reward amount
     */
    public Incentive fetchIncentive(Transaction transaction)
    {
        return restTemplate.postForObject(
                "http://localhost:8080/incentive",
                transaction,
                Incentive.class
        );
    }
}