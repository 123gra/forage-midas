package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveApiClient {
    private final RestTemplate restTemplate;

    public IncentiveApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Incentive getIncentive(Transaction transaction) {
        try {
            return restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
        } catch (Exception e) {
            // Log error and return default/null incentive to prevent crash
            // System.out.println("Error calling incentive API: " + e.getMessage());
            return new Incentive(0);
        }
    }
}
