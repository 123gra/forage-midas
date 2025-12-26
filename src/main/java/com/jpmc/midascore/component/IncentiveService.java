package com.jpmc.midascore.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;

@Service
public class IncentiveService {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    private final RestTemplate restTemplate;

    public IncentiveService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Call the incentives API to get the incentive amount for a transaction
     *
     * @param transaction The transaction to get incentive for
     * @return The incentive amount (>= 0)
     */
    public float getIncentive(Transaction transaction) {
        try {
            logger.info("🎯 Calling incentive API with transaction: Transaction {{senderId={}, recipientId={}, amount={}}}",
                    transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
            
            // POST the transaction to the API and get the Incentive response
            Incentive incentive = restTemplate.postForObject(
                    INCENTIVE_API_URL,
                    transaction,
                    Incentive.class
            );

            if (incentive != null) {
                logger.info("🎯 Received incentive: {} for transaction amount: {}", 
                        incentive.getAmount(), transaction.getAmount());
                return incentive.getAmount();
            } else {
                logger.warn("Incentive API returned null, using 0");
                return 0.0f;
            }
        } catch (Exception e) {
            logger.error("Error calling incentive API: {}", e.getMessage());
            return 0.0f;
        }
    }
}