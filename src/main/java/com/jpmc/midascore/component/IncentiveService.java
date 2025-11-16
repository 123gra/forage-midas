package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveService {
    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);
    private final RestTemplate restTemplate;
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    public IncentiveService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Incentive getIncentive(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(INCENTIVE_API_URL, transaction, Incentive.class);
            if (incentive == null) {
                logger.warn("Incentive API returned null for transaction: {}", transaction);
                return new Incentive(0.0f);
            }
            logger.info("Received incentive amount: {} for transaction: {}", incentive.getAmount(), transaction);
            return incentive;
        } catch (Exception e) {
            logger.error("Error calling incentive API for transaction: {}", transaction, e);
            // Return 0 incentive if API call fails
            return new Incentive(0.0f);
        }
    }
}