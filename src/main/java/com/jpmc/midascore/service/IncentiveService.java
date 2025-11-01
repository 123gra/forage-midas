package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.model.Incentive;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {
    private final RestTemplate restTemplate;

    public IncentiveService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Incentive getIncentive(long senderId, long recipientId, double amount) {
        try {
            Transaction transaction = new Transaction(senderId, recipientId, (float) amount);
            String url = "http://localhost:8080/incentive";
            return restTemplate.postForObject(url, transaction, Incentive.class);
        } catch (Exception e) {
            // If API call fails, return incentive of 0
            return new Incentive(0.0);
        }
    }
}

