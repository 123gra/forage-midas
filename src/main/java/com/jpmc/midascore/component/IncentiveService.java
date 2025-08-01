package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.Incentive;
import com.jpmc.midascore.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {

    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);

    private final RestTemplate restTemplate;
    private final String incentiveApiUrl;

    public IncentiveService(RestTemplate restTemplate,
                            @Value("${incentive.api.url:http://localhost:8080/incentive}") String incentiveApiUrl) {
        this.restTemplate = restTemplate;
        this.incentiveApiUrl = incentiveApiUrl;
    }

    public float getIncentiveAmount(Transaction transaction) {
        try {
            logger.info("Calling incentive API for transaction: {}", transaction);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);

            Incentive incentive = restTemplate.postForObject(
                    incentiveApiUrl,
                    request,
                    Incentive.class
            );

            if (incentive != null) {
                logger.info("Received incentive amount: {}", incentive.getAmount());
                return incentive.getAmount();
            } else {
                logger.warn("Received null incentive response");
                return 0;
            }

        } catch (Exception e) {
            logger.error("Error calling incentive API: {}", e.getMessage());
            return 0;
        }
    }
}