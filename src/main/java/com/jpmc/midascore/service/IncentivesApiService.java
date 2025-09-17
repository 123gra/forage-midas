package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class IncentivesApiService {

    private static final Logger logger = LoggerFactory.getLogger(IncentivesApiService.class);
    
    private final RestTemplate restTemplate;
    private final String incentiveApiUrl;

    public IncentivesApiService(RestTemplate restTemplate, 
                               @Value("${incentives.api.url:http://localhost:8080/incentive}") String incentiveApiUrl) {
        this.restTemplate = restTemplate;
        this.incentiveApiUrl = incentiveApiUrl;
    }

    public float calculateIncentive(Transaction transaction) {
        try {
            logger.debug("Calling incentives API for transaction: {}", transaction);
            
            Incentive incentive = restTemplate.postForObject(incentiveApiUrl, transaction, Incentive.class);
            
            if (incentive != null) {
                logger.debug("Received incentive: {}", incentive.getAmount());
                return incentive.getAmount();
            } else {
                logger.warn("Received null incentive response for transaction: {}", transaction);
                return 0.0f;
            }
            
        } catch (RestClientException e) {
            logger.error("Failed to call incentives API for transaction: {}", transaction, e);
            return 0.0f; // Default to no incentive if API call fails
        }
    }
}
