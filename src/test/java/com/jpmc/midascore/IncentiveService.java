package com.jpmc.midascore;

// Task 4

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);

    private final RestTemplate restTemplate;

    public IncentiveService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Incentive fetchIncentive(Transaction transaction) {
        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive", transaction, Incentive.class);

        if (incentive == null) {
            logger.warn("⚠️ Incentive API returned NULL for transaction: {}", transaction);
        } else {
            logger.info("✅ Incentive API returned: {}", incentive.getAmount());
        }

        return incentive;
    }
}
