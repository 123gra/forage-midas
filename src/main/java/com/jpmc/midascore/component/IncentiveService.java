package com.jpmc.midascore.component;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.midascore.exception.DependencyException;
import com.jpmc.midascore.foundation.Transaction;

@Component
public class IncentiveService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final static String INCENTIVE_URL = "http://localhost:8080/incentive";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public float getIncentive(Transaction transaction) throws DependencyException {
        try {
            String responseEntityStr = restTemplate.postForObject(INCENTIVE_URL, transaction, String.class);
            return Float.parseFloat(objectMapper.readTree(responseEntityStr).get("amount").asText());
        } catch (Exception e) {
            throw new DependencyException(e.getMessage());
        }
    }
    
}
